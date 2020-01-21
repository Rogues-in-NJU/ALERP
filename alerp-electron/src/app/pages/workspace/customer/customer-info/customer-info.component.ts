import { Component, OnInit } from "@angular/core";
import { CustomerService } from "../../../../core/services/customer.service";
import { ActivatedRoute, Router } from "@angular/router";
import { ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../../../../core/model/result-vm";
import { CustomerSpecialPriceVO, CustomerVO } from "../../../../core/model/customer";
import { Objects, StringUtils } from "../../../../core/services/util.service";
import { HttpErrorResponse } from "@angular/common/http";
import { NzMessageService } from "ng-zorro-antd";
import { ProductVO } from "../../../../core/model/product";
import { BehaviorSubject, Observable, of } from "rxjs";
import { ProductService } from "../../../../core/services/product.service";
import { debounceTime, map, switchMap } from "rxjs/operators";
import { RefreshableTab } from "../../tab/tab.component";
import { TabService } from "../../../../core/services/tab.service";

@Component({
  selector: 'customer-info',
  templateUrl: './customer-info.component.html',
  styleUrls: [ './customer-info.component.less' ]
})
export class CustomerInfoComponent implements RefreshableTab, OnInit {

  isLoading: boolean = true;
  customerId: number;
  customerData: CustomerVO;
  customerDataValidate: any = {
    name: null,
    period: null,
    payDate: null
  };

  customerDataCache: CustomerVO;

  isInfoEditing: boolean = false;
  isInfoSaving: boolean = false;

  editCache: {
    _id?: number,
    data?: TempCustomerSpecialPriceVO,
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
  customerSpecialPriceCountIndex: number = 0;
  searchProducts: ProductVO[];
  searchChange$: BehaviorSubject<string> = new BehaviorSubject('');
  isProductsLoading: boolean = false;

  periodFormatter: any = (value: number) => {
    if (Objects.valid(value)) {
      return `${value} 个月`;
    } else {
      return ``;
    }
  };
  payDateFormatter: any = (value: number) => {
    if (Objects.valid(value)) {
      return `${value} 日`;
    } else {
      return ``;
    }
  };
  cashFormatter: any = (value: number) => {
    if (Objects.valid(value)) {
      return `¥ ${value}`;
    } else {
      return `¥`;
    }
  };

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private customer: CustomerService,
    private product: ProductService,
    private message: NzMessageService,
    private tab: TabService
  ) {

  }

  ngOnInit(): void {
    this.customerId = this.route.snapshot.params[ 'id' ];
    this.refresh();

    const getProducts: any = (name: string) => {
      if (StringUtils.isEmpty(name)) {
        return of([]);
      }
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
    const optionList$: Observable<ProductVO[]> = this.searchChange$
      .asObservable()
      .pipe(debounceTime(500))
      .pipe(switchMap(getProducts));
    optionList$.subscribe((data: ProductVO[]) => {
      this.searchProducts = data;
      this.isProductsLoading = false;
    });
  }

  startInfoEdit(): void {
    this.isInfoEditing = true;
    for (const k in this.customerDataValidate) {
      this.customerDataValidate[ k ] = null;
    }
    this.customerDataCache = {
      name: null,
      type: null,
      period: null,
      payDate: null
    };
    Object.assign(this.customerDataCache, this.customerData);
  }

  onInputValueChange(value: any, name: string): void {
    if (!Objects.valid(value) || StringUtils.isEmpty(value + '')) {
      this.customerDataValidate[ name ] = 'error';
    } else {
      this.customerDataValidate[ name ] = null;
    }
  }

  saveInfoEdit(): void {
    let valid: boolean = true;
    if (!Objects.valid(this.customerData.name)) {
      this.customerDataValidate.name = 'error';
      valid = false;
    }
    if (!Objects.valid(this.customerData.period)) {
      this.customerDataValidate.perid = 'error';
      valid = false;
    }
    if (!Objects.valid(this.customerData.name)) {
      this.customerDataValidate.name = 'error';
      valid = false;
    }
    if (!valid) {
      return;
    }
    Object.assign(this.customerData, this.customerDataCache);
    this.isInfoSaving = true;
    this.saveCustomer(this.customerData);
  }

  cancelInfoEdit(): void {
    this.customerDataCache = null;
    this.isInfoEditing = false;
    for (const k in this.customerDataValidate) {
      this.customerDataValidate[ k ] = null;
    }
  }

  addSpecialPriceRow(): void {
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存商品列表的更改!');
      return;
    }
    let item: CustomerSpecialPriceVO = {
      id: null,
      productId: null,
      productName: '',
      price: 0,
      priceType: 1
    };
    item[ '_id' ] = this.customerSpecialPriceCountIndex++;
    this.customerData.specialPrices = [
      item,
      ...this.customerData.specialPrices
    ];
    this.startEditSpecialPrice(item[ '_id' ], true);
  }

  onProductSearch(value: string): void {
    this.isProductsLoading = true;
    this.searchChange$.next(value);
  }

  onChangeSelectedProduct(event: TempProductVO): void {
    this.editCache.data.productId = event.id;
    this.editCache.data.productName = event.name;
  }

  startEditSpecialPrice(_id: number, isAdd?: boolean): void {
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存特价列表的更改!');
      return;
    }
    const index = this.customerData.specialPrices.findIndex(item => item[ '_id' ] === _id);
    if (index === -1) {
      this.message.error('没有该特价条目!');
      return;
    }
    this.editCache._id = _id;
    this.editCache.data = {};
    const t: CustomerSpecialPriceVO = this.customerData.specialPrices[ index ];
    Object.assign(this.editCache.data, t);
    console.log(this.editCache.data);
    this.editCache.currentProduct = {
      id: t.productId,
      name: t.productName
    };
    this.editCache.product = this.editCache.currentProduct;
    if (Objects.valid(isAdd) && isAdd) {
      this.editCache.isAdd = isAdd;
    } else {
      this.editCache.isAdd = false;
    }
  }

  confirmSpecialPriceDelete(_id: number): void {
    this.customerData.specialPrices = this.customerData.specialPrices.filter(item => item[ '_id' ] !== _id);
    this.saveCustomer(this.customerData);
  }

  cancelSpecialPriceEdit(_id: number): void {
    if (Objects.valid(this.editCache.isAdd) && this.editCache.isAdd) {
      this.customerData.specialPrices = this.customerData.specialPrices.filter(item => item[ '_id' ] !== _id);
    }
    Object.assign(this.editCache, this.defaultEditCache);
  }

  saveSpecialPriceEdit(_id: number): void {
    if (_id !== this.editCache._id) {
      return;
    }
    const index = this.customerData.specialPrices.findIndex(item => item[ '_id' ] === _id);
    Object.assign(this.customerData.specialPrices[ index ], this.editCache.data);
    Object.assign(this.editCache, this.defaultEditCache);
    if (!Objects.valid(this.customerData.specialPrices[index].id)) {
      delete this.customerData.specialPrices[index].id;
    }
    delete this.customerData.specialPrices[index]['_id'];
    this.saveCustomer(this.customerData);
  }

  refresh(): void {
    this.isLoading = true;
    this.isInfoEditing = false;
    Object.assign(this.editCache, this.defaultEditCache);
    this.customer.find(this.customerId)
      .subscribe((res: ResultVO<CustomerVO>) => {
        if (!Objects.valid(res)) {
          return;
        }
        this.isLoading = false;
        this.customerData = res.data;
        if (Objects.valid(this.customerData.specialPrices)) {
          this.customerData.specialPrices.forEach(item => {
            item[ '_id' ] = this.customerSpecialPriceCountIndex++;
          });
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  saveCustomer(customer: CustomerVO): void {
    this.isInfoSaving = true;
    this.customer.save(customer)
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
        this.message.error(error.message);
        this.refresh();
      }, () => {
        this.refresh();
        this.isInfoSaving = false;
        this.isInfoEditing = false;
        this.tab.refreshEvent.emit({
          url: '/workspace/customer/list'
        });
      });
  }

}

interface TempProductVO {

  id: number;
  name: string;

  [ key: string ]: any;

}

interface TempCustomerSpecialPriceVO {

  id?: number;
  productId?: number;
  productName?: string;
  price?: string;
  priceType?: number;

}
