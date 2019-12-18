import { Component, OnInit } from "@angular/core";
import { CustomerService } from "../../../../core/services/customer.service";
import { ActivatedRoute, Router } from "@angular/router";
import { ResultVO } from "../../../../core/model/result-vm";
import { CustomerSpecialPriceVO, CustomerVO } from "../../../../core/model/customer";
import { Objects } from "../../../../core/services/util.service";
import { HttpErrorResponse } from "@angular/common/http";
import { NzMessageService } from "ng-zorro-antd";
import { ProductVO } from "../../../../core/model/product";
import { BehaviorSubject, Observable } from "rxjs";
import { ProductService } from "../../../../core/services/product.service";
import { debounceTime, map, switchMap } from "rxjs/operators";

@Component({
  selector: 'customer-info',
  templateUrl: './customer-info.component.html',
  styleUrls: ['./customer-info.component.less']
})
export class CustomerInfoComponent implements OnInit {

  isLoading: boolean = true;
  customerId: number;
  customerData: CustomerVO;

  customerDataCache: CustomerVO;

  isInfoEditing: boolean = false;
  isInfoSaving: boolean = false;

  editCache: { _id?: number, data?: TempCustomerSpecialPriceVO, product?: TempProductVO, isAdd?: boolean } = {};
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
    private message: NzMessageService
  ) {

  }

  ngOnInit(): void {
    this.customerId = this.route.snapshot.params['id'];
    this.reload();

    const getProducts: any = (name: string) => {
      return this.product
        .findAll({})
        .pipe(
          map((res: ResultVO<ProductVO[]>) => res.data)
        );
    }
    const optionList$: Observable<ProductVO[]> = this.searchChange$
      .asObservable()
      .pipe(debounceTime(500))
      .pipe(switchMap(getProducts));
    optionList$.subscribe((data: ProductVO[]) => {
      this.searchProducts = data;
      this.isProductsLoading = false;
    })
  }

  startInfoEdit(): void {
    this.isInfoEditing = true;
    this.customerDataCache = {
      name: '',
      type: 1,
      period: 1,
      payDate: 20
    };
    Object.assign(this.customerDataCache, this.customerData);
    console.log(this.customerDataCache);
  }

  saveInfoEdit(): void {
    this.isInfoSaving = true;
    this.customer.save(this.customerDataCache)
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== 200) {
          return;
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      }, () => {
        this.reload();
        this.isInfoSaving = false;
        this.isInfoEditing = false;
      });
  }

  cancelInfoEdit(): void {
    this.customerDataCache = null;
    this.isInfoEditing = false;
  }

  onProductSearch(value: string): void {
    this.isProductsLoading = true;
    this.searchChange$.next(value);
  }

  onChangeSelectedProduct(event: TempProductVO): void {
    this.editCache.data.productId = event.id;
    this.editCache.data.productName = event.name;
  }

  reload(): void {
    this.isLoading = true;
    this.customer.find(this.customerId)
      .subscribe((res: ResultVO<CustomerVO>) => {
        if (!Objects.valid(res)) {
          return;
        }
        this.isLoading = false;
        this.customerData = res.data;
        if (Objects.valid(this.customerData.specialPrices)) {
          this.customerData.specialPrices.forEach(item => {
            item['_id'] = this.customerSpecialPriceCountIndex ++;
          });
        }
        console.log(this.customerData);
      });
  }

}

interface TempProductVO {

  id: number;
  name: string;
  [key: string]: any;

}

interface TempCustomerSpecialPriceVO {

  id?: number;
  productId?: number;
  productName?: string;
  price?: string;

}
