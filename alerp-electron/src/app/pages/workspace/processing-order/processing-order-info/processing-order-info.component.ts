import { Component, OnInit } from "@angular/core";
import { TabService } from "../../../../core/services/tab.service";
import { ActivatedRoute, Router } from "@angular/router";
import { ProcessingOrderService } from "../../../../core/services/processing-order.service";
import { NzMessageService } from "ng-zorro-antd";
import { ProcessingOrderProductVO, ProcessingOrderVO } from "../../../../core/model/processing-order";
import { ResultCode, ResultVO } from "../../../../core/model/result-vm";
import { HttpErrorResponse } from "@angular/common/http";
import { Objects, SpecificationUtils, StringUtils } from "../../../../core/services/util.service";
import { ProductVO } from "../../../../core/model/product";
import { BehaviorSubject, Observable } from "rxjs";
import { ProductService } from "../../../../core/services/product.service";
import { debounceTime, map, switchMap } from "rxjs/operators";

@Component({
  selector: 'processing-order-info',
  templateUrl: './processing-order-info.component.html',
  styleUrls: [ './processing-order-info.component.less' ]
})
export class ProcessingOrderInfoComponent implements OnInit {

  isLoading: boolean = true;
  processingOrderId: number;
  processingOrderData: ProcessingOrderVO;

  editCache: {
    _id?: number,
    data?: TempProcessingOrderInfoProductVO,
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
  processingOrderInfoProductCountIndex: number = 0;
  searchProducts: ProductVO[];
  searchChanges$: BehaviorSubject<string> = new BehaviorSubject('');
  isProductsLoading: boolean = false;
  specificationAutoComplete: { label: string, value: string }[] = [];
  specificationValidateStatus: string = null;

  cashFormatter: any = (value: number) => {
    if (Objects.valid(value)) {
      return `¥ ${value}`;
    } else {
      return `¥`;
    }
  };

  constructor(
    private closeTab: TabService,
    private route: ActivatedRoute,
    private router: Router,
    private processingOrder: ProcessingOrderService,
    private product: ProductService,
    private message: NzMessageService,
  ) {

  }

  ngOnInit(): void {
    this.processingOrderId = this.route.snapshot.params[ 'id' ];
    this.reload();

    const getProducts: any = (name: string) => {
      const t: Observable<ResultVO<ProductVO[]>>
        = <Observable<ResultVO<ProductVO[]>>>this.product
        .findAll({});
      return t.pipe(map(res => res.data));
    };
    const optionList$: Observable<ProductVO[]> = this.searchChanges$
      .asObservable()
      .pipe(debounceTime(500))
      .pipe(switchMap(getProducts));
    optionList$.subscribe((data: ProductVO[]) => {
      this.searchProducts = data;
      this.isProductsLoading = false;
    });
  }

  addProductRow(): void {
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存加工商品列表的更改!');
      return;
    }
    let item: ProcessingOrderProductVO = {
      id: 0,
      productId: 0,
      productName: '',
      type: 1,
      density: 1.00,
      productSpecification: '1*1*1',
      specification: '1*1*1',
      quantity: 1,
      expectedWeight: 1
    };
    item[ '_id' ] = this.processingOrderInfoProductCountIndex++;
    this.processingOrderData.products = [
      item,
      ...this.processingOrderData.products
    ];
    this.startEditProduct(item[ '_id' ], true);
  }

  onProductSearch(value: string): void {
    this.isProductsLoading = true;
    this.searchChanges$.next(value);
  }

  onChangeSelectedProduct(event: TempProductVO): void {
    this.editCache.data.productId = event.id;
    this.editCache.data.productName = event.name;
    this.editCache.data.density = event.density;
  }

  startEditProduct(_id: number, isAdd?: boolean): void {
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存加工商品列表的更改!');
      return;
    }
    const index = this.processingOrderData.products.findIndex(item => item[ '_id' ] === _id);
    if (index === -1) {
      this.message.error('没有该加工商品条目!');
      return;
    }
    this.editCache._id = _id;
    this.editCache.data = {};
    const t: ProcessingOrderProductVO = this.processingOrderData.products[ index ];
    Object.assign(this.editCache.data, t);
    this.editCache.currentProduct = {
      id: t.productId,
      name: t.productName,
      density: t.density
    };
    this.editCache.product = this.editCache.currentProduct;
    if (Objects.valid(isAdd) && isAdd) {
      this.editCache.isAdd = isAdd;
    } else {
      this.editCache.isAdd = false;
    }
  }

  confirmProductDelete(_id: number): void {
    this.processingOrderData.products = this.processingOrderData.products.filter(item => item[ '_id' ] !== _id);
  }

  cancelProductEdit(_id: number): void {
    if (Objects.valid(this.editCache.isAdd) && this.editCache.isAdd) {
      this.processingOrderData.products = this.processingOrderData.products.filter(item => item[ '_id' ] !== _id);
    }
    Object.assign(this.editCache, this.defaultEditCache);
  }

  saveProductEdit(_id: number): void {
    if (_id !== this.editCache._id) {
      return;
    }
    // 验证规格格式
    console.log(this.editCache.data.specification);
    if (!SpecificationUtils.valid(this.editCache.data.specification)) {
      this.specificationValidateStatus = 'error';
      return;
    }
    // TODO: 提交远程刷新
    const index = this.processingOrderData.products.findIndex(item => item[ '_id' ] === _id);
    Object.assign(this.processingOrderData.products[ index ], this.editCache.data);
    Object.assign(this.editCache, this.defaultEditCache);
    this.processingOrder.saveProduct(this.processingOrderData.products[ index ])
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      }, () => {
        this.reload();
      });
  }

  onSpecificationInput(value: string): void {
    if (!Objects.valid(value)) {
      this.specificationValidateStatus = 'error';
    }
    this.specificationValidateStatus = null;
    this.specificationAutoComplete = [];
    let splits: string[] = value.split('*');
    if (splits.length === 0) {
      return;
    }
    splits = splits.filter(item => !StringUtils.isEmpty(item));
    splits = splits.map(item => item.trim());
    console.log(splits);
    if (splits.length === 1) {
      if (splits[0].startsWith(SpecificationUtils.FAI_U)
        || splits[0].startsWith(SpecificationUtils.FAI_L)) {
        splits[0] = splits[0].substring(1);
        if (splits[0].search(SpecificationUtils.NUM_PATT) === -1) {
          this.specificationValidateStatus = 'error';
          return;
        }
        this.specificationAutoComplete = [{
          label: SpecificationUtils.FAI_U + parseFloat(splits[0]),
          value: SpecificationUtils.FAI_U + parseFloat(splits[0])
        }];
      } else {
        if (splits[0].search(SpecificationUtils.NUM_PATT) === -1) {
          this.specificationValidateStatus = 'error';
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
        this.specificationValidateStatus = 'error';
        return;
      }
      if (splits[0].startsWith(SpecificationUtils.FAI_U)
        || splits[0].startsWith(SpecificationUtils.FAI_L)) {
        splits[0] = splits[0].substring(1);
        if (splits[0].search(SpecificationUtils.NUM_PATT) === -1) {
          this.specificationValidateStatus = 'error';
          return;
        }
        this.specificationAutoComplete = [{
          label: SpecificationUtils.FAI_U + parseFloat(splits[0]) + '*' + parseFloat(splits[1]),
          value: SpecificationUtils.FAI_U + parseFloat(splits[0]) + '*' + parseFloat(splits[1])
        }];
      } else {
        if (splits[0].search(SpecificationUtils.NUM_PATT) === -1) {
          this.specificationValidateStatus = 'error';
          return;
        }
        this.specificationAutoComplete = [{
          label: SpecificationUtils.FAI_U + parseFloat(splits[0]) + '*' + parseFloat(splits[1]),
          value: SpecificationUtils.FAI_U + parseFloat(splits[0]) + '*' + parseFloat(splits[1])
        }, {
          label: '' + parseFloat(splits[0]) + '*' + parseFloat(splits[1]),
          value: '' + parseFloat(splits[0]) + '*' + parseFloat(splits[1])
        }];
        this.specificationValidateStatus = 'error';
      }
      return;
    }
    if (splits.length === 3) {
      for (const s in splits) {
        if (s.search(SpecificationUtils.NUM_PATT) === -1) {
          this.specificationValidateStatus = 'error';
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

  reload(): void {
    Object.assign(this.editCache, this.defaultEditCache);
    this.processingOrder.find(this.processingOrderId)
      .subscribe((res: ResultVO<ProcessingOrderVO>) => {
        if (!Objects.valid(res)) {
          return;
        }
        this.isLoading = false;
        this.processingOrderData = res.data;
        if (Objects.valid(this.processingOrderData.products)) {
          this.processingOrderData.products.forEach(item => {
            item[ '_id' ] = this.processingOrderInfoProductCountIndex++;
          })
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

}

interface TempProductVO {

  id: number;
  name: string;
  density: number;

  [ key: string ]: any;

}

interface TempProcessingOrderInfoProductVO {

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


