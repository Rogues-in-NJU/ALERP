import {Component, OnInit, ViewChild, ElementRef} from "@angular/core";
import {RefreshTabEvent, TabService} from "../../../../core/services/tab.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ProcessingOrderService} from "../../../../core/services/processing-order.service";
import {NzMessageService} from "ng-zorro-antd";
import {ProcessingOrderProductVO, ProcessingOrderVO} from "../../../../core/model/processing-order";
import {ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../../../../core/model/result-vm";
import {HttpErrorResponse} from "@angular/common/http";
import {Objects, SpecificationUtils, StringUtils} from "../../../../core/services/util.service";
import {ProductVO} from "../../../../core/model/product";
import {BehaviorSubject, Observable, of} from "rxjs";
import {ProductService} from "../../../../core/services/product.service";
import {debounceTime, map, switchMap} from "rxjs/operators";
import {RefreshableTab} from "../../tab/tab.component";
import {ENgxPrintComponent} from "e-ngx-print";

@Component({
  selector: 'processing-order-info',
  templateUrl: './processing-order-info.component.html',
  styleUrls: ['./processing-order-info.component.less']
})
export class ProcessingOrderInfoComponent implements RefreshableTab, OnInit {

  isLoading: boolean = true;
  isPreview: boolean = false;
  processingOrderId: number;
  processingOrderData: ProcessingOrderVO;

  editCache: {
    _id?: number,
    data?: TempProcessingOrderProductVO,
    product?: TempProductVO,
    currentProduct?: TempProductVO,
    isAdd?: boolean
  } = {};
  defaultEditCache: any = {
    _id: null,
    data: null,
    product: null,
    currentProduct: null,
    isAdd: false
  };
  editCacheValidateStatus: any = {
    productId: null,
    specification: null,
    quantity: null,
    expectedWeight: null
  };
  processingOrderInfoProductCountIndex: number = 0;
  searchProducts: ProductVO[];
  searchChanges$: BehaviorSubject<string> = new BehaviorSubject('');
  isProductsLoading: boolean = false;
  specificationAutoComplete: {label: string, value: string}[] = [];

  printCSS: string[];
  printStyle: string;

  date: string;

  constructor(private tab: TabService,
              private route: ActivatedRoute,
              private router: Router,
              private processingOrder: ProcessingOrderService,
              private product: ProductService,
              private message: NzMessageService,
              private elRef: ElementRef) {
    this.printCSS = ['http://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css'];

    this.printStyle =
      `
        th, td {
            color: blue !important;
        }
        `;

    let date = new Date();
    let y = date.getFullYear();
    let m = date.getUTCMonth() + 1;
    let d = date.getDate();
    this.date = y + '-' + m + '-' + d;
  }

  ngOnInit(): void {
    this.isLoading = true;
    this.processingOrderId = this.route.snapshot.params['id'];
    this.refresh();

    const getProducts: any = (name: string) => {
      // if (StringUtils.isEmpty(name)) {
      //   return of([]);
      // }
      const t: Observable<ResultVO<TableResultVO<ProductVO>>>
        = <Observable<ResultVO<TableResultVO<ProductVO>>>>this.product
        .findAll(Object.assign(new TableQueryParams(), {
          pageIndex: 1,
          pageSize: 100000,
          name: name
        }));
      return t.pipe(map(res => {
        if (!Objects.valid(res)) {
          return [];
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return [];
        }
        return res.data.result;
      }));
    };
    const optionList$: Observable<ProductVO[]> = this.searchChanges$
      .asObservable()
      .pipe(debounceTime(500))
      .pipe(switchMap(getProducts));
    optionList$.subscribe((data: ProductVO[]) => {
      this.searchProducts = data;
      this.isProductsLoading = false;
    });
    this.tab.refreshEvent.subscribe((res: RefreshTabEvent) => {
      if (res.url === this.router.url) {
        this.refresh();
      }
    });
  }

  addProductRow(): void {
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存加工商品列表的更改!');
      return;
    }
    let item: ProcessingOrderProductVO = {
      id: null,
      processingOrderId: this.processingOrderData.id,
      productId: null,
      productName: null,
      type: null,
      density: null,
      productSpecification: null,
      specification: null,
      quantity: null,
      expectedWeight: null,
      processingOrderUpdatedAt: this.processingOrderData.updatedAt
    };
    item['_id'] = this.processingOrderInfoProductCountIndex++;
    this.processingOrderData.products = [
      item,
      ...this.processingOrderData.products
    ];
    this.startEditProduct(item['_id'], true);
  }

  copyProductRow(_id: number): void {
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存加工商品列表的更改!');
      return;
    }
    const index = this.processingOrderData.products.findIndex(item => item['_id'] === _id);
    if (index === -1) {
      this.message.error('没有可复制的商品条目!');
      return;
    }
    let item: ProcessingOrderProductVO = {
      id: null,
      processingOrderId: this.processingOrderData.id,
      productId: null,
      productName: null,
      type: null,
      density: null,
      productSpecification: null,
      specification: null,
      quantity: null,
      expectedWeight: null,
      processingOrderUpdatedAt: this.processingOrderData.updatedAt
    };
    Object.assign(item, this.processingOrderData.products[index], {
      id: null,  // 重要，不能把id也复制过去
      processingOrderUpdatedAt: this.processingOrderData.updatedAt
    });
    item['_id'] = this.processingOrderInfoProductCountIndex++;
    this.processingOrderData.products = [
      ...this.processingOrderData.products.filter((item, i) => i < index),
      item,
      ...this.processingOrderData.products.filter((item, i) => i >= index)
    ];
    this.startEditProduct(item['_id'], true);
  }

  onProductSearch(value: string): void {
    this.isProductsLoading = true;
    this.searchChanges$.next(value);
  }

  onChangeSelectedProduct(event: TempProductVO): void {
    this.editCache.data.productId = event.id;
    this.editCache.data.productName = event.name;
    this.editCache.data.type = event.type;
    this.editCache.data.density = event.density;

    this.calculateExpectedWeight();
  }

  startEditProduct(_id: number, isAdd?: boolean): void {
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存加工商品列表的更改!');
      return;
    }
    const index = this.processingOrderData.products.findIndex(item => item['_id'] === _id);
    if (index === -1) {
      this.message.error('没有该加工商品条目!');
      return;
    }
    this.editCache._id = _id;
    this.editCache.data = {};
    const t: ProcessingOrderProductVO = this.processingOrderData.products[index];
    Object.assign(this.editCache.data, t);
    this.editCache.currentProduct = {
      id: t.productId,
      name: t.productName,
      type: t.type,
      density: t.density
    };
    this.editCache.product = this.editCache.currentProduct;
    if (Objects.valid(isAdd) && isAdd) {
      this.editCache.isAdd = isAdd;
    } else {
      this.editCache.isAdd = false;
    }

    Object.values(this.editCacheValidateStatus).forEach(item => item = null);
  }

  confirmProductDelete(_id: number): void {
    const index: number = this.processingOrderData.products.findIndex(item => item['_id'] === _id);
    this.processingOrder.deleteProduct(this.processingOrderData.products[index].id)
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          this.message.error('删除失败!');
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.message.success('删除成功!');
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
        this.refresh();
      }, () => {
        this.refresh();
      });
  }

  cancelProductEdit(_id: number): void {
    if (Objects.valid(this.editCache.isAdd) && this.editCache.isAdd) {
      this.processingOrderData.products = this.processingOrderData.products.filter(item => item['_id'] !== _id);
    }
    Object.assign(this.editCache, this.defaultEditCache);
  }

  saveProductEdit(_id: number): void {
    if (_id !== this.editCache._id) {
      return;
    }
    // 验证规格格式
    if (!this.checkProcessingOrderProductValid()) {
      return;
    }
    const index = this.processingOrderData.products.findIndex(item => item['_id'] === _id);
    Object.assign(this.processingOrderData.products[index], this.editCache.data);
    Object.assign(this.editCache, this.defaultEditCache);
    this.processingOrder.saveProduct(this.processingOrderData.products[index])
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          this.message.error('修改失败!');
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.message.success('修改成功!');
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
        this.refresh();
      }, () => {
        this.refresh();
      });
  }

  onSpecificationInput(value: string): void {
    this.calculateExpectedWeight();
    if (!Objects.valid(value)) {
      this.editCacheValidateStatus.specification = 'error';
    }
    this.editCacheValidateStatus.specification = null;
    this.specificationAutoComplete = [];
    let splits: string[] = value.split('*');
    if (splits.length === 0) {
      return;
    }
    splits = splits.filter(item => !StringUtils.isEmpty(item));
    splits = splits.map(item => item.trim());
    if (this.editCache.product.type !== 2
      && (splits[0].startsWith(SpecificationUtils.FAI_U)
      || splits[0].startsWith(SpecificationUtils.FAI_L))) { // 不是铝棒
      this.editCacheValidateStatus.specification = 'error';
      return;
    }
    if (splits.length === 1) {
      if (splits[0].startsWith(SpecificationUtils.FAI_U)
        || splits[0].startsWith(SpecificationUtils.FAI_L)) {
        splits[0] = splits[0].substring(1);
        if (splits[0].search(SpecificationUtils.NUM_PATT) === -1) {
          this.editCacheValidateStatus.specification = 'error';
          return;
        }
        this.specificationAutoComplete = [{
          label: SpecificationUtils.FAI_U + parseFloat(splits[0]),
          value: SpecificationUtils.FAI_U + parseFloat(splits[0])
        }];
      } else {
        if (splits[0].search(SpecificationUtils.NUM_PATT) === -1) {
          this.editCacheValidateStatus.specification = 'error';
          return;
        }
        this.specificationAutoComplete = [{
          label: SpecificationUtils.FAI_U + parseFloat(splits[0]),
          value: SpecificationUtils.FAI_U + parseFloat(splits[0])
        }, {
          label: '' + parseFloat(splits[0]),
          value: '' + parseFloat(splits[0])
        }];
      }
      return;
    }
    if (splits.length === 2) {
      if (splits[1].search(SpecificationUtils.NUM_PATT) === -1) {
        this.editCacheValidateStatus.specification = 'error';
        return;
      }
      if (splits[0].startsWith(SpecificationUtils.FAI_U)
        || splits[0].startsWith(SpecificationUtils.FAI_L)) {
        splits[0] = splits[0].substring(1);
        if (splits[0].search(SpecificationUtils.NUM_PATT) === -1) {
          this.editCacheValidateStatus.specification = 'error';
          return;
        }
        this.specificationAutoComplete = [{
          label: SpecificationUtils.FAI_U + parseFloat(splits[0]) + '*' + parseFloat(splits[1]),
          value: SpecificationUtils.FAI_U + parseFloat(splits[0]) + '*' + parseFloat(splits[1])
        }];
      } else {
        if (splits[0].search(SpecificationUtils.NUM_PATT) === -1) {
          this.editCacheValidateStatus.specification = 'error';
          return;
        }
        this.specificationAutoComplete = [{
          label: SpecificationUtils.FAI_U + parseFloat(splits[0]) + '*' + parseFloat(splits[1]),
          value: SpecificationUtils.FAI_U + parseFloat(splits[0]) + '*' + parseFloat(splits[1])
        }, {
          label: '' + parseFloat(splits[0]) + '*' + parseFloat(splits[1]),
          value: '' + parseFloat(splits[0]) + '*' + parseFloat(splits[1])
        }];
        this.editCacheValidateStatus.specification = 'error';
      }
      return;
    }
    if (splits.length === 3) {
      if (this.editCache.product.type === 2) {
        this.editCacheValidateStatus.specification = 'error';
        return;
      }
      for (const s of splits) {
        // console.log(s);
        if (s.search(SpecificationUtils.NUM_PATT) === -1) {
          this.editCacheValidateStatus.specification = 'error';
          return;
        }
      }
      this.specificationAutoComplete = [{
        label: `${parseFloat(splits[0])}*${parseFloat(splits[1])}*${parseFloat(splits[2])}`,
        value: `${parseFloat(splits[0])}*${parseFloat(splits[1])}*${parseFloat(splits[2])}`
      }];
      return;
    }
  }

  @ViewChild('print1', {static: false})
  printComponent: ENgxPrintComponent;
  showPrint: boolean = false;

  printComplete() {
    this.processingOrder.finishPrint(this.processingOrderData.id)
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          this.message.error('打印状态更新失败!');
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.message.success('打印状态更新成功!');
      }, (error: HttpErrorResponse) => {
        this.message.error('打印出错!');
        this.refresh();
        this.tab.refreshEvent.emit({
          url: '/workspace/processing-order/list'
        });
      }, () => {
        this.refresh();
        this.tab.refreshEvent.emit({
          url: '/workspace/processing-order/list'
        });
      });
    this.showPrint = false;
  }

  handleCancel(): void {
    this.isPreview = false;
  }

  preview(): void {
    this.isPreview = true;
    this.showPrint = true;
  }

  customPrint(print: string) {
    const printHTML: any = this.elRef.nativeElement.childNodes[4];
    this.printComponent.print(printHTML);

    this.handleCancel();
  }

  refresh(): void {
    Object.assign(this.editCache, this.defaultEditCache);
    this.processingOrder.find(this.processingOrderId)
      .subscribe((res: ResultVO<ProcessingOrderVO>) => {
        if (!Objects.valid(res)) {
          return;
        }
        this.processingOrderData = res.data;
        this.isLoading = false;
        if (Objects.valid(this.processingOrderData.products)) {
          this.processingOrderData.products.forEach(item => {
            item.processingOrderId = this.processingOrderData.id;
            item['_id'] = this.processingOrderInfoProductCountIndex++;
            item.processingOrderUpdatedAt = this.processingOrderData.updatedAt;
          });
        }
        // console.log(this.processingOrderData);
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      });
  }

  calculateExpectedWeight(): void {
    this.editCache.data.expectedWeight
      = SpecificationUtils.calculateWeight(this.editCache.data.specification,
      this.editCache.product.density, this.editCache.data.quantity);
  }

  onQuantityChange(): void {
    if (this.checkQuantity()) {
      this.calculateExpectedWeight();
    }
  }

  checkQuantity(): boolean {
    if (Objects.valid(this.editCache.data.quantity)) {
      this.editCacheValidateStatus.quantity = null;
      // 做计算
      return true;
    } else {
      this.editCacheValidateStatus.quantity = 'error';
      return false;
    }
  }

  checkExpectedWeight(): boolean {
    if (Objects.valid(this.editCache.data.expectedWeight)) {
      this.editCacheValidateStatus.expectedWeight = null;
      return true;
    } else {
      this.editCacheValidateStatus.expectedWeight = 'error';
      return false;
    }
  }

  checkProcessingOrderProductValid(): boolean {
    if (!Objects.valid(this.editCache.data)) {
      return false;
    }
    let isValid: boolean = true;
    if (!Objects.valid(this.editCache.data.productId) || this.editCache.data.productId === 0) {
      this.editCacheValidateStatus.productId = 'error';
      isValid = false;
    }
    if (!this.checkQuantity()) {
      isValid = false;
    }
    if (!this.checkExpectedWeight()) {
      isValid = false;
    }
    // 验证规格格式
    if (!SpecificationUtils.valid(this.editCache.data.specification)) {
      this.editCacheValidateStatus.specification = 'error';
      isValid = false;
    }
    return isValid;
  }

}

interface TempProductVO {

  id: number;
  name: string;
  type: number,
  density: number;

  [ key: string ]: any;

}

interface TempProcessingOrderProductVO {

  id?: number;
  productId?: number;
  productName?: string;
  type?: number;
  density?: number;
  productSpecification?: string;
  specification?: string;
  quantity?: number;
  expectedWeight?: number;

}


