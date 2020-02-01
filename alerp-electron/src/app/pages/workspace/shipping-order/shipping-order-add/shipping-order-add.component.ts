import {Component, OnInit} from "@angular/core";
import {ClosableTab, RefreshableTab} from "../../tab/tab.component";
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
export class ShippingOrderAddComponent implements RefreshableTab, OnInit, ClosableTab{

  isLoading: boolean = true;
  shippingOrderCode: string = "";
  shippingOrderData: ShippingOrderInfoVO = {
    processingOrderIdsCodes: [],
    products: [],
    tax: false,
    cash: 0,
    floatingCash: 0,
    receivableCash: 0,
  };

  //modal
  shippingOrderAddVisible: boolean = false;
  shippingOrderAddOkLoading: boolean = false;
  addShippingOrder_customerName: string = null;
  addShippingOrder_allProcessingOrderList: ProcessingOrderVO[] =[];

  addShippingOrder_isLoading: boolean = false;
  addShippingOrder_totalPages: number = 1;
  addShippingOrder_pageIndex: number = 1;
  addShippingOrder_pageSize: number = 10;

  isAllDisplayDataChecked = false;
  isOperating = false;
  isIndeterminate = false;
  listOfDisplayData: ProcessingOrderVO[] = [];
  mapOfCheckedId: { [key: number]: boolean } = {};
  numberOfChecked = 0;
  checkedCustomerId: number = -1;

  //editproduct
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
    price: null,
    weight: null,
    priceType: null,
    cash: null,
  };

  defaultEditCacheValidateStatus: any = {
    productId: null,
    price: null,
    weight: null,
    priceType: null,
    cash: null,
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
    private message: NzMessageService,
    private tab: TabService,
  ) {
    // this.shippingOrderCode = this.route.snapshot.params['code'];
  }

  ngOnInit(): void {
    this.showAddModal();
    Object.assign(this.editCache, this.defaultEditCache);

    const getProducts: any = (name: string) => {
      const t: Observable<ResultVO<TableResultVO<ProductVO>>>
        = <Observable<ResultVO<TableResultVO<ProductVO>>>>this.product
        .findAll(Object.assign(new TableQueryParams(), {
          pageIndex: 1,
          pageSize: 1000,
          type: 3
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
      id: null,
      processingOrderCode: '',
      productId: null,
      productName: null,
      type: null,
      specification: null,
      quantity: 1,
      priceType: 2,
      price: null,
      expectedWeight: null,
      weight: null,
      cash: null,
    };
    item[ '_isEditable'] = true;
    item[ '_isSunhao' ] = true;
    item[ '_id' ] = this.shippingOrderInfoProductCountIndex++;
    this.shippingOrderData.products = [
      item,
      ...this.shippingOrderData.products
    ];
    this.isAddSunHao = true;
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

  caculateCashForProduct():void{

    // if(Objects.valid(this.editCache.data.cash)){
    //   return;
    // }

    if(Objects.valid(this.editCache.data.price) &&
      Objects.valid(this.editCache.data.priceType)){
      if(this.editCache.data.priceType === 1){
        //yuan/kg
        if(Objects.valid(this.editCache.data.weight)){
          this.editCache.data.cash =  this.editCache.data.price * this.editCache.data.weight;
        }
      } else if(this.editCache.data.priceType === 2){
        //yuan/jian
        this.editCache.data.cash =  this.editCache.data.price * this.editCache.data.quantity;
      }
    }
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
    Object.assign(this.editCacheValidateStatus, this.defaultEditCacheValidateStatus)
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

    const index = this.shippingOrderData.products.findIndex(item => item[ '_id' ] === _id);
    Object.assign(this.shippingOrderData.products[ index ], this.editCache.data);
    Object.assign(this.editCache, this.defaultEditCache);
    Object.assign(this.editCacheValidateStatus, this.defaultEditCacheValidateStatus)

    this.shippingOrderData.cash = this.shippingOrderData.products
      .filter(product => Objects.valid(product.cash))
      .map(product => product.cash)
      .reduce((pre, cur) => pre + cur);
    this.shippingOrderData.receivableCash = this.shippingOrderData.cash - this.shippingOrderData.floatingCash;

    this.isAddSunHao = false;
  }

  confirmAddShippingOrder():void {

    for(let product of this.shippingOrderData.products){
      if(!Objects.valid(product.cash)){
        this.message.warning("请确认是否录入每件商品的价格!");
        return;
      }
    }

    console.log(this.shippingOrderData.receivableCash + "!!!!!!!!!!!!!");

    if(!Objects.valid(this.shippingOrderData.receivableCash)
      || Objects.isNaN(this.shippingOrderData.receivableCash)){
      this.message.warning("应收金额有误，请重新录入！");
      return;
    }

    if(Objects.isNaN(this.shippingOrderData.tax)){
      this.message.warning("请确认是否含税！");
      return;
    }


    console.log(this.shippingOrderData);
    this.shippingOrder.save(this.shippingOrderData)
      .subscribe((res: ResultVO<any>) => {
        console.log(res);
        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.message.success('新增成功!');
        this.tabClose();
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      }, () => {

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

    if (!this.checkModelNotNull('productId')) {
      isValid = false;
    }
    if (!this.checkModelNotNull('price')) {
      isValid = false;
    }
    if (!this.checkModelNotNull('weight')) {
      isValid = false;
    }
    if (!this.checkModelNotNull('priceType')) {
      isValid = false;
    }
    if (!this.checkModelNotNull('cash')) {
      isValid = false;
    }

    return isValid;
  }

  checkModelNotNullAndCaculateCash(name: string): boolean{
    //顺便自动计算一下价格
    this.caculateCashForProduct();

    return this.checkModelNotNull(name);
  }
  checkModelNotNull(name: string): boolean {

    //损耗只需要增加cash
    if(this.isAddSunHao){
      if(name !== "cash" && name !== "productId"){
        return true;
      }
    }
    if (Objects.valid(this.editCache.data[name]) && !StringUtils.isEmpty(this.editCache.data[name])) {
      this.editCacheValidateStatus[name] = null;
      console.log('here');
      return true;
    } else {
      this.editCacheValidateStatus[name] = 'error';
      return false;
    }
  }

  refresh(): void {
  }

  modifyFloatingCash(): void{
    this.shippingOrderData.floatingCash = this.shippingOrderData.cash - this.shippingOrderData.receivableCash;
  }

  modifyReceivableCash():void{
    this.shippingOrderData.receivableCash = this.shippingOrderData.cash - this.shippingOrderData.floatingCash;
  }

  tabClose(): void {
    this.tab.closeEvent.emit({
      url: this.router.url,
      goToUrl: '/workspace/shipping-order/list',
      refreshUrl: '/workspace/shipping-order/list',
      routeConfig: this.route.snapshot.routeConfig
    });
  }

  //modal

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
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        const tableResult: TableResultVO<ProcessingOrderVO> = res.data;

        this.addShippingOrder_totalPages = tableResult.totalPages;
        this.addShippingOrder_pageIndex = tableResult.pageIndex;
        // this.addShippingOrder_pageSize = tableResult.pageSize;
        this.addShippingOrder_allProcessingOrderList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
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
      this.shippingOrderData.processingOrderIdsCodes.push({
        processingOrderId: processingOrder.id,
        processingOrderCode: processingOrder.code,
      });

      this.processingOrder.find(processingOrder.id)
        .subscribe((res: ResultVO<ProcessingOrderVO>) => {
          console.log(res);
          if (!Objects.valid(res)) {
            this.message.error("请求失败！");
            return;
          }
          if (res.code !== ResultCode.SUCCESS.code) {
            this.message.error(res.message);
            return;
          }
          let processingOrderInfoVO : ProcessingOrderVO = res.data;

          if (Objects.valid(processingOrderInfoVO.products)) {
            for(let product of processingOrderInfoVO.products){
              let shippingProduct: ShippingOrderProductInfoVO = {};
              shippingProduct.processingOrderId = processingOrder.id;
              shippingProduct.processingOrderCode = processingOrder.code;
              shippingProduct.productId = product.productId;
              shippingProduct.productName = product.productName;
              shippingProduct.type = product.type;
              shippingProduct.density = product.density;
              shippingProduct.specification = product.specification;
              shippingProduct.quantity = product.quantity;
              shippingProduct.expectedWeight = product.expectedWeight;

              if(!product.isEditable){
                shippingProduct.price = product.specialPrice;
                shippingProduct.priceType = product.specialPriceType;
              }
              shippingProduct.isEditable = product.isEditable;
              shippingProduct['_id'] = this.shippingOrderInfoProductCountIndex++;
              this.shippingOrderData.products = [
                shippingProduct,
                ...this.shippingOrderData.products
              ];
            }
          }
        }, (error: HttpErrorResponse) => {
          this.message.error('网络异常，请检查网络或者尝试重新登录!');
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
      status: 1, //未完成
    };

    if (!StringUtils.isEmpty(this.addShippingOrder_customerName)) {
      Object.assign(queryParams, {
        customerName: this.addShippingOrder_customerName
      });
      this.addShippingOrder_customerName = null;
    }

    console.log(queryParams);
    this.processingOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ProcessingOrderVO>>) => {
        console.log(res);

        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        const tableResult: TableResultVO<ProcessingOrderVO> = res.data;

        this.addShippingOrder_totalPages = tableResult.totalPages;
        this.addShippingOrder_pageIndex = tableResult.pageIndex;
        // this.addShippingOrder_pageSize = tableResult.pageSize;
        this.addShippingOrder_allProcessingOrderList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
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
