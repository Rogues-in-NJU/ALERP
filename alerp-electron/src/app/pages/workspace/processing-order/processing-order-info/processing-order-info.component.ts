import { Component, OnInit } from "@angular/core";
import { TabService } from "../../../../core/services/tab.service";
import { ActivatedRoute, Router } from "@angular/router";
import { ProcessingOrderService } from "../../../../core/services/processing-order.service";
import { NzMessageService } from "ng-zorro-antd";
import { ProcessingOrderProductVO, ProcessingOrderVO } from "../../../../core/model/processing-order";
import { QueryParams, ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../../../../core/model/result-vm";
import { HttpErrorResponse } from "@angular/common/http";
import { Objects, SpecificationUtils, StringUtils } from "../../../../core/services/util.service";
import { ProductVO } from "../../../../core/model/product";
import { BehaviorSubject, Observable } from "rxjs";
import { ProductService } from "../../../../core/services/product.service";
import { debounceTime, map, switchMap } from "rxjs/operators";
import { RefreshableTab } from "../../tab/tab.component";
import { CustomerVO } from "../../../../core/model/customer";

@Component({
  selector: 'processing-order-info',
  templateUrl: './processing-order-info.component.html',
  styleUrls: [ './processing-order-info.component.less' ]
})
export class ProcessingOrderInfoComponent implements RefreshableTab, OnInit {

  isLoading: boolean = true;
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
  specificationAutoComplete: { label: string, value: string }[] = [];

  printCSS: string[];
  printStyle: string;

  constructor(
    private closeTab: TabService,
    private route: ActivatedRoute,
    private router: Router,
    private processingOrder: ProcessingOrderService,
    private product: ProductService,
    private message: NzMessageService,
  ) {
    this.printCSS = ['http://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css'];

    this.printStyle =
      `
        th, td {
            color: blue !important;
        }
        `;
  }

  printComplete() {
    console.log('打印完成！');
  }

  ngOnInit(): void {
    this.processingOrderId = this.route.snapshot.params[ 'id' ];
    this.refresh();

    const getProducts: any = (name: string) => {
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
      productName: '',
      type: null,
      density: null,
      productSpecification: '',
      specification: '',
      quantity: null,
      expectedWeight: null
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
    this.editCache.data.type = event.type;
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
    // TODO: 提交后端修改
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
    if (!this.checkProcessingOrderProductValid()) {
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
        this.refresh();
      });
  }

  onSpecificationInput(value: string): void {
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
    console.log(splits);
    if (splits.length === 1) {
      if (splits[ 0 ].startsWith(SpecificationUtils.FAI_U)
        || splits[ 0 ].startsWith(SpecificationUtils.FAI_L)) {
        splits[ 0 ] = splits[ 0 ].substring(1);
        if (splits[ 0 ].search(SpecificationUtils.NUM_PATT) === -1) {
          this.editCacheValidateStatus.specification = 'error';
          return;
        }
        this.specificationAutoComplete = [ {
          label: SpecificationUtils.FAI_U + parseFloat(splits[ 0 ]),
          value: SpecificationUtils.FAI_U + parseFloat(splits[ 0 ])
        } ];
      } else {
        if (splits[ 0 ].search(SpecificationUtils.NUM_PATT) === -1) {
          this.editCacheValidateStatus.specification = 'error';
          return;
        }
        this.specificationAutoComplete = [ {
          label: SpecificationUtils.FAI_U + parseFloat(splits[ 0 ]),
          value: SpecificationUtils.FAI_U + parseFloat(splits[ 0 ])
        }, {
          label: '' + parseFloat(splits[ 0 ]),
          value: '' + parseFloat(splits[ 0 ])
        } ];
      }
      return;
    }
    if (splits.length === 2) {
      if (splits[ 1 ].search(SpecificationUtils.NUM_PATT) === -1) {
        this.editCacheValidateStatus.specification = 'error';
        return;
      }
      if (splits[ 0 ].startsWith(SpecificationUtils.FAI_U)
        || splits[ 0 ].startsWith(SpecificationUtils.FAI_L)) {
        splits[ 0 ] = splits[ 0 ].substring(1);
        if (splits[ 0 ].search(SpecificationUtils.NUM_PATT) === -1) {
          this.editCacheValidateStatus.specification = 'error';
          return;
        }
        this.specificationAutoComplete = [ {
          label: SpecificationUtils.FAI_U + parseFloat(splits[ 0 ]) + '*' + parseFloat(splits[ 1 ]),
          value: SpecificationUtils.FAI_U + parseFloat(splits[ 0 ]) + '*' + parseFloat(splits[ 1 ])
        } ];
      } else {
        if (splits[ 0 ].search(SpecificationUtils.NUM_PATT) === -1) {
          this.editCacheValidateStatus.specification = 'error';
          return;
        }
        this.specificationAutoComplete = [ {
          label: SpecificationUtils.FAI_U + parseFloat(splits[ 0 ]) + '*' + parseFloat(splits[ 1 ]),
          value: SpecificationUtils.FAI_U + parseFloat(splits[ 0 ]) + '*' + parseFloat(splits[ 1 ])
        }, {
          label: '' + parseFloat(splits[ 0 ]) + '*' + parseFloat(splits[ 1 ]),
          value: '' + parseFloat(splits[ 0 ]) + '*' + parseFloat(splits[ 1 ])
        } ];
        this.editCacheValidateStatus.specification = 'error';
      }
      return;
    }
    if (splits.length === 3) {
      for (const s of splits) {
        console.log(s);
        if (s.search(SpecificationUtils.NUM_PATT) === -1) {
          this.editCacheValidateStatus.specification = 'error';
          return;
        }
      }
      this.specificationAutoComplete = [ {
        label: `${parseFloat(splits[ 0 ])}*${parseFloat(splits[ 1 ])}*${parseFloat(splits[ 2 ])}`,
        value: `${parseFloat(splits[ 0 ])}*${parseFloat(splits[ 1 ])}*${parseFloat(splits[ 2 ])}`
      } ];
      return;
    }
  }

  refresh(): void {
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
            // TODO: check
            // item.processingOrderId = this.processingOrderId;
            item[ '_id' ] = this.processingOrderInfoProductCountIndex++;
          })
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  checkModelNotNull(name: string): boolean {
    if (Objects.valid(this.editCache.data[name]) && !StringUtils.isEmpty(this.editCache.data[name])) {
      this.editCacheValidateStatus[name] = null;
      console.log('here');
      return true;
    } else {
      this.editCacheValidateStatus[name] = 'error';
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
    if (!this.checkModelNotNull('quantity')) {
      isValid = false;
    }
    if (!this.checkModelNotNull('expectedWeight')) {
      isValid = false;
    }
    // 验证规格格式
    console.log(this.editCache.data.specification);
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


