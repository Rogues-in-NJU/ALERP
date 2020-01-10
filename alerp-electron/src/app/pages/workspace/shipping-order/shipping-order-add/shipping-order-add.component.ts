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
import {QueryParams, ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../../../../core/model/result-vm";
import {debounceTime, map, switchMap} from "rxjs/operators";
import {Objects, SpecificationUtils, StringUtils} from "../../../../core/services/util.service";
import {ProcessingOrderProductVO, ProcessingOrderVO} from "../../../../core/model/processing-order";
import {HttpErrorResponse} from "@angular/common/http";
import {ProcessingOrderService} from "../../../../core/services/processing-order.service";

@Component({
  selector: 'shipping-order-add',
  templateUrl: './shipping-order-add.component.html',
  styleUrls: [ './shipping-order-add.component.less' ]
})
export class ShippingOrderAddComponent implements RefreshableTab, OnInit{

  //todo 特价

  //todo 自动计价

  //todo 输入框上下调整问题

  //todo 详情跳转至欠款明细


  isLoading: boolean = true;
  shippingOrderCode: string = "";
  shippingOrderData: ShippingOrderInfoVO = {
    processingOrderCodes: [],
    products: [],
    cash: 0,
    floatingCash: 0,
    receivableCash: 0,
  };

  cash: number = 0;
  floatingCash: number = 0;
  receivableCash: number = 0;

  //modal
  shippingOrderAddVisible: boolean = false;
  shippingOrderAddOkLoading: boolean = false;
  addShippingOrder_customerName: string = null;
  addShippingOrder_allProcessingOrderList: ProcessingOrderVO[] =[];

  addShippingOrder_isLoading: boolean = false;
  addShippingOrder_totalPages: number = 1;
  addShippingOrder_pageIndex: number = 1;
  addShippingOrder_pageSize: number = 2;

  isAllDisplayDataChecked = false;
  isOperating = false;
  isIndeterminate = false;
  listOfDisplayData: ProcessingOrderVO[] = [];
  mapOfCheckedId: { [key: number]: boolean } = {};
  numberOfChecked = 0;
  checkedCustomerId: number = -1;

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

  shippingOrderInfoProductCountIndex: number = 0;
  searchProducts: ProductVO[];
  searchChanges$: BehaviorSubject<string> = new BehaviorSubject('');
  isProductsLoading: boolean = false;
  specificationAutoComplete: { label: string, value: string }[] = [];

  isAddSunHao: boolean = false;

  constructor(
    private closeTabService: TabService,
    private route: ActivatedRoute,
    private router: Router,
    private shippingOrder: ShippingOrderService,
    private processingOrder: ProcessingOrderService,
    private product: ProductService,
    private message: NzMessageService
  ) {
    // this.shippingOrderCode = this.route.snapshot.params['code'];
  }

  ngOnInit(): void {
    this.showAddModal();
    Object.assign(this.editCache, this.defaultEditCache);

    //todo 只能出损耗
    const getProducts: any = (name: string) => {
      const t: Observable<ResultVO<TableResultVO<ProductVO>>>
        = <Observable<ResultVO<TableResultVO<ProductVO>>>>this.product
        .findAll(Object.assign(new TableQueryParams(), {
          pageIndex: 1,
          pageSize: 100000
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
    const productOptionList$: Observable<ProductVO[]> = this.searchChanges$
      .asObservable()
      .pipe(debounceTime(500))
      .pipe(switchMap(getProducts));
    productOptionList$.subscribe((data: ProductVO[]) => {
      this.searchProducts = data;
      this.isProductsLoading = false;
    });
  }

  addProductRow(): void {
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存加工商品列表的更改!');
      return;
    }
    let item: ShippingOrderProductInfoVO = {
      id: -1,
      processingOrderCode: '',
      productId: -1,
      productName: '损耗',
      type: 1,
      specification: '',
      quantity: 1,
      price: null,
      expectedWeight: null,
      weight: 0,
      cash: null,
    };
    item[ '_id' ] = this.shippingOrderInfoProductCountIndex++;
    this.shippingOrderData.products = [
      item,
      ...this.shippingOrderData.products
    ];
    this.isAddSunHao = true;
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
    this.isAddSunHao = false;
  }

  saveProductEdit(_id: number): void {
    if (_id !== this.editCache._id) {
      return;
    }
    // 验证规格格式
    if (!this.checkShippingOrderProductValid()) {
      return;
    }
    // TODO: 提交远程刷新 -> 不提交了，放到vo里
    // const index = this.shippingOrderData.products.findIndex(item => item[ '_id' ] === _id);
    // Object.assign(this.shippingOrderData.products[ index ], this.editCache.data);
    // Object.assign(this.editCache, this.defaultEditCache);
    // this.shippingOrder.saveProduct(this.shippingOrderData.products[ index ])
    //   .subscribe((res: ResultVO<any>) => {
    //     if (!Objects.valid(res)) {
    //       return;
    //     }
    //     if (res.code !== ResultCode.SUCCESS.code) {
    //       return;
    //     }
    //   }, (error: HttpErrorResponse) => {
    //     this.message.error(error.message);
    //   }, () => {
    //     this.refresh();
    //   });

    //todo 写个累加金额的方法
    this.shippingOrderData.cash += this.editCache.data.cash;
    this.isAddSunHao = false;
  }

  confirmAddShippingOrder():void {
    //todo
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

  showAddModal(): void {
    this.shippingOrderAddVisible = true;
    this.resetAddProcessingOrderModal();

    const queryParams: TableQueryParams = {
      pageIndex: this.addShippingOrder_pageIndex,
      pageSize: 1000,
      status: 1, //未完成
    };
    this.processingOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ProcessingOrderVO>>) => {
        console.log(res)
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        const tableResult: TableResultVO<ProcessingOrderVO> = res.data;

        this.addShippingOrder_totalPages = tableResult.totalPages;
        this.addShippingOrder_pageIndex = tableResult.pageIndex;
        this.addShippingOrder_pageSize = tableResult.pageSize;
        this.addShippingOrder_allProcessingOrderList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  confirmAdd(): void {
    this.shippingOrderAddOkLoading = true;

    for(let processingOrder of this.addShippingOrder_allProcessingOrderList){
      if(!this.mapOfCheckedId[processingOrder.id]){
        continue;
      }

      this.shippingOrderData.customerId = processingOrder.customerId;
      this.shippingOrderData.customerName = processingOrder.customerName;
      this.shippingOrderData.processingOrderCodes.push(processingOrder.code);

      this.processingOrder.find(processingOrder.id)
        .subscribe((res: ResultVO<ProcessingOrderVO>) => {
          console.log(res);
          if (!Objects.valid(res)) {
            return;
          }
          let processingOrderInfoVO : ProcessingOrderVO = res.data;

          if (Objects.valid(processingOrderInfoVO.products)) {
            for(let product of processingOrderInfoVO.products){
              let shippingProduct: ShippingOrderProductInfoVO = {};
              shippingProduct.processingOrderId = processingOrder.id;
              shippingProduct.processingOrderCode = processingOrder.code;
              shippingProduct.productId = product.id;
              shippingProduct.productName = product.productName;
              shippingProduct.type = product.type;
              shippingProduct.density = product.density;
              shippingProduct.specification = product.specification;
              shippingProduct.quantity = product.quantity;
              shippingProduct.expectedWeight = product.expectedWeight;

              shippingProduct['_id'] = this.shippingOrderInfoProductCountIndex++;
              this.shippingOrderData.products = [
                shippingProduct,
                ...this.shippingOrderData.products
              ];
            }
          }
        }, (error: HttpErrorResponse) => {
          this.message.error(error.message);
        });
    }

    this.isLoading = false;
    this.shippingOrderAddOkLoading = false;
    this.shippingOrderAddVisible = false;
    this.message.success('添加成功!');
    console.log(this.shippingOrderData)
  }

  cancelAdd(): void {
    this.shippingOrderAddVisible = false;
    this.resetAddProcessingOrderModal();
  }

  searchProcessingOrderBycustomername(): void {
    this.addShippingOrder_isLoading = true;

    this.resetAddProcessingOrderModal();

    const queryParams: TableQueryParams = {
      pageIndex: this.addShippingOrder_pageIndex,
      pageSize: this.addShippingOrder_pageSize,
      customerName: this.addShippingOrder_customerName,
      status: 1, //未完成
    };
    this.processingOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ProcessingOrderVO>>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        const tableResult: TableResultVO<ProcessingOrderVO> = res.data;

        this.addShippingOrder_totalPages = tableResult.totalPages;
        this.addShippingOrder_pageIndex = tableResult.pageIndex;
        this.addShippingOrder_pageSize = tableResult.pageSize;
        this.addShippingOrder_allProcessingOrderList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });

    this.addShippingOrder_isLoading = false;
  }

  currentPageDataChange($event: ProcessingOrderVO[]): void {
    this.listOfDisplayData = $event;
    this.refreshStatus(null);
  }

  refreshStatus(id: number): void {
    this.isAllDisplayDataChecked = this.listOfDisplayData
      .filter(item => this.isProcessingOrderAvailable(item.customerId))
      .every(item => this.mapOfCheckedId[item.id]);
    this.isIndeterminate =
      this.listOfDisplayData.filter(item => this.isProcessingOrderAvailable(item.customerId))
        .some(item => this.mapOfCheckedId[item.id]) &&
      !this.isAllDisplayDataChecked;
    this.numberOfChecked = this.addShippingOrder_allProcessingOrderList
      .filter(item => this.mapOfCheckedId[item.id]).length;

    if(id !== null){
      if(this.checkedCustomerId === -1){
        this.checkedCustomerId = id;
      } else if(this.numberOfChecked === 0){
        this.checkedCustomerId = -1;
      }
    }

  }

  checkAll(value: boolean): void {
    this.listOfDisplayData.filter(item =>this.isProcessingOrderAvailable(item.customerId))
      .forEach(item => (this.mapOfCheckedId[item.id] = value));
    if(!value){
      this.checkedCustomerId = -1;
    }
    this.refreshStatus(null);
  }

  isProcessingOrderAvailable(customerId: number): boolean{
    if(this.checkedCustomerId === -1){
      return true;
    }
    if(customerId === this.checkedCustomerId){
      return true;
    }
    return false;
  }

  resetAddProcessingOrderModal():void{
    this.isAllDisplayDataChecked = false;
    this.isIndeterminate = false;
    this.listOfDisplayData= [];
    this.mapOfCheckedId= {};
    this.numberOfChecked = 0;
    this.checkedCustomerId= -1;
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
  priceType?: number;
  price?: number;
  expectedWeight?: number;
  weight?: number;
  cash?: number;
}
