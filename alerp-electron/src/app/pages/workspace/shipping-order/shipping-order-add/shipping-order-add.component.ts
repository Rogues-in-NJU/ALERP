import {Component, OnInit} from "@angular/core";
import {RefreshableTab} from "../../tab/tab.component";
import {ShippingOrderInfoVO, ShippingOrderProductInfoVO} from "../../../../core/model/shipping-order";
import {ProductVO} from "../../../../core/model/product";
import {BehaviorSubject, Observable} from "rxjs";
import {TabService} from "../../../../core/services/tab.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ShippingOrderService} from "../../../../core/services/shipping-order.service";
import {ProductService} from "../../../../core/services/product.service";
import {NzMessageService} from "ng-zorro-antd";
import {QueryParams, ResultCode, ResultVO} from "../../../../core/model/result-vm";
import {debounceTime, map, switchMap} from "rxjs/operators";
import {Objects, SpecificationUtils, StringUtils} from "../../../../core/services/util.service";
import {ProcessingOrderProductVO} from "../../../../core/model/processing-order";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'shipping-order-add',
  templateUrl: './shipping-order-add.component.html',
  styleUrls: [ './shipping-order-add.component.less' ]
})
export class ShippingOrderAddComponent implements RefreshableTab, OnInit{

  isLoading: boolean = true;
  shippingOrderCode: string;
  shippingOrderData: ShippingOrderInfoVO;

  cash: number = 0;
  floatingCash: number = 0;
  receivableCash: number = 0;

  editCache: {
    _id?: number,
    data?: TempShippingOrderProductVO,
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


  constructor(
    private closeTabService: TabService,
    private route: ActivatedRoute,
    private router: Router,
    private shippingOrder: ShippingOrderService,
    private product: ProductService,
    private message: NzMessageService
  ) {
    this.shippingOrderCode = this.route.snapshot.params['code'];
  }

  ngOnInit(): void {
    this.shippingOrderCode = this.route.snapshot.params[ 'id' ];
    this.reload();

    const getProducts: any = (name: string) => {
      const t: Observable<ResultVO<ProductVO[]>>
        = <Observable<ResultVO<ProductVO[]>>>this.product
        .findAll(Object.assign(new QueryParams(), {}));
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
      productSpecification: '',
      specification: '',
      quantity: 1,
      expectedWeight: 1
    };
    item[ '_id' ] = this.processingOrderInfoProductCountIndex++;
    this.shippingOrderData.products = [
      item,
      ...this.shippingOrderData.products
    ];
    this.startEditProduct(item[ '_id' ], true);
  }

  tabClose(): void {
    this.closeTabService.closeEvent.emit({
      url: this.router.url,
      refreshUrl: null,
      routeConfig: this.route.snapshot.routeConfig
    });
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
    const index = this.shippingOrderData.products.findIndex(item => item[ '_id' ] === _id);
    if (index === -1) {
      this.message.error('没有该加工商品条目!');
      return;
    }
    this.editCache._id = _id;
    this.editCache.data = {};
    const t: ShippingOrderProductInfoVO = this.shippingOrderData.products[ index ];
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

    Object.values(this.editCacheValidateStatus).forEach(item => item = null);
  }

  confirmProductDelete(_id: number): void {
    this.shippingOrderData.products = this.shippingOrderData.products.filter(item => item[ '_id' ] !== _id);
  }

  cancelProductEdit(_id: number): void {
    if (Objects.valid(this.editCache.isAdd) && this.editCache.isAdd) {
      this.shippingOrderData.products = this.shippingOrderData.products.filter(item => item[ '_id' ] !== _id);
    }
    Object.assign(this.editCache, this.defaultEditCache);
  }

  saveProductEdit(_id: number): void {
    if (_id !== this.editCache._id) {
      return;
    }
    // 验证规格格式
    if (!this.checkShippingOrderProductValid()) {
      return;
    }
    // TODO: 提交远程刷新
    const index = this.shippingOrderData.products.findIndex(item => item[ '_id' ] === _id);
    Object.assign(this.shippingOrderData.products[ index ], this.editCache.data);
    Object.assign(this.editCache, this.defaultEditCache);
    this.shippingOrder.saveProduct(this.shippingOrderData.products[ index ])
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
  checkShippingOrderProductValid(): boolean {
    if (!Objects.valid(this.editCache.data)) {
      return false;
    }
    let isValid: boolean = true;
    if (this.editCache.data.productId === 0) {
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


  reload(): void {
    Object.assign(this.editCache, this.defaultEditCache);
    this.shippingOrder.find(this.shippingOrderCode)
      .subscribe((res: ResultVO<ShippingOrderInfoVO>) => {
        if (!Objects.valid(res)) {
          return;
        }
        this.isLoading = false;
        this.shippingOrderData = res.data;
        if (Objects.valid(this.shippingOrderData.products)) {
          this.shippingOrderData.products.forEach(item => {
            item[ '_id' ] = this.processingOrderInfoProductCountIndex++;
          })
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
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
  }
}

interface TempProductVO {

  id: number;
  name: string;
  density: number;

  [ key: string ]: any;

}

interface TempShippingOrderProductVO {

  id?: number;
  productId?: number;
  productName?: string;
  processingOrderId?: number;
  processingOrderCode?: string;
  type?: number;
  density?: number;
  productSpecification?: string;
  specification?: string;
  quantity?: number;
  price?: number;
  expectedWeight?: number;
  weight?: number;
  cash?: number;
}
