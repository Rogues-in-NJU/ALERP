import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ProcessingOrderProductVO } from "../../../../core/model/processing-order";
import { ProductVO } from "../../../../core/model/product";
import { BehaviorSubject, Observable } from "rxjs";
import { ActivatedRoute, Router } from "@angular/router";
import { ProcessingOrderService } from "../../../../core/services/processing-order.service";
import { ProductService } from "../../../../core/services/product.service";
import { NzMessageService } from "ng-zorro-antd";
import { Objects, SpecificationUtils, StringUtils } from "../../../../core/services/util.service";
import { QueryParams, ResultVO } from "../../../../core/model/result-vm";
import { debounceTime, map, switchMap } from "rxjs/operators";
import { CustomerVO } from "../../../../core/model/customer";
import { CustomerService } from "../../../../core/services/customer.service";

@Component({
  selector: 'processing-order-add',
  templateUrl: './processing-order-add.component.html',
  styleUrls: [ './processing-order-add.component.less' ]
})
export class ProcessingOrderAddComponent implements OnInit {

  isSaving: boolean = false;

  processingOrderForm: FormGroup;
  products: ProcessingOrderProductVO[] = [];
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
  searchProductChanges$: BehaviorSubject<string> = new BehaviorSubject('');
  isProductsLoading: boolean = false;
  searchCustomers: CustomerVO[];
  searchCustomerChanges$: BehaviorSubject<string> = new BehaviorSubject('');
  isCustomersLoading: boolean = false;

  specificationAutoComplete: { label: string, value: string }[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private processingOrder: ProcessingOrderService,
    private product: ProductService,
    private customer: CustomerService,
    private message: NzMessageService
  ) {

  }

  ngOnInit(): void {
    Object.assign(this.editCache, this.defaultEditCache);
    this.processingOrderForm = this.fb.group({
      customerId: [ null, Validators.required ],
      salesman: [ null ]
    });
    const getProducts: any = (name: string) => {
      const t: Observable<ResultVO<ProductVO[]>>
        = <Observable<ResultVO<ProductVO[]>>>this.product
        .findAll(Object.assign(new QueryParams(), {}));
      return t.pipe(map(res => res.data));
    };
    const productOptionList$: Observable<ProductVO[]> = this.searchProductChanges$
      .asObservable()
      .pipe(debounceTime(500))
      .pipe(switchMap(getProducts));
    productOptionList$.subscribe((data: ProductVO[]) => {
      this.searchProducts = data;
      this.isProductsLoading = false;
    });
    const getCustomers: any = (name: string) => {
      const t: Observable<ResultVO<CustomerVO[]>>
        = <Observable<ResultVO<CustomerVO[]>>>this.customer
        .findAll(Object.assign(new QueryParams(), {}));
      return t.pipe(map(res => res.data));
    };
    const customerOptionList$: Observable<CustomerVO[]> = this.searchCustomerChanges$
      .asObservable()
      .pipe(debounceTime(500))
      .pipe(switchMap(getCustomers));
    customerOptionList$.subscribe((data: CustomerVO[]) => {
      this.searchCustomers = data;
      this.isCustomersLoading = false;
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
    this.products = [
      item,
      ...this.products
    ];
    this.startEditProduct(item[ '_id' ], true);
  }

  onProductSearch(value: string): void {
    this.isProductsLoading = true;
    this.searchProductChanges$.next(value);
  }

  onChangeSelectedProduct(event: TempProductVO): void {
    this.editCache.data.productId = event.id;
    this.editCache.data.productName = event.name;
    this.editCache.data.density = event.density;
  }

  onCustomerSearch(value: string): void {
    this.isCustomersLoading = true;
    this.searchCustomerChanges$.next(value);
  }

  startEditProduct(_id: number, isAdd?: boolean): void {
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存加工商品列表的更改!');
      return;
    }
    const index = this.products.findIndex(item => item[ '_id' ] === _id);
    if (index === -1) {
      this.message.error('没有该加工商品条目!');
      return;
    }
    this.editCache._id = _id;
    this.editCache.data = {};
    const t: ProcessingOrderProductVO = this.products[ index ];
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
    this.products = this.products.filter(item => item[ '_id' ] !== _id);
  }

  cancelProductEdit(_id: number): void {
    if (Objects.valid(this.editCache.isAdd) && this.editCache.isAdd) {
      this.products = this.products.filter(item => item[ '_id' ] !== _id);
    }
    Object.assign(this.editCache, this.defaultEditCache);
  }

  saveProductEdit(_id: number): void {
    if (_id !== this.editCache._id) {
      return;
    }

    if (!this.checkProcessingOrderProductValid()) {
      return;
    }

    const index = this.products.findIndex(item => item[ '_id' ] === _id);
    Object.assign(this.products[ index ], this.editCache.data);
    Object.assign(this.editCache, this.defaultEditCache);
    console.log(this.products[ index ]);
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

}

interface TempProductVO {

  id: number;
  name: string;
  density: number;

  [ key: string ]: any;

}

interface TempProcessingOrderProductVO {

  productId?: number;
  productName?: string;
  type?: number;
  density?: number;
  productSpecification?: string;
  specification?: string;
  quantity?: number;
  expectedWeight?: number;

}
