import { Component, OnInit } from "@angular/core";
import { ClosableTab } from "../../tab/tab.component";
import { TabService } from "../../../../core/services/tab.service";
import { PurchaseOrderService } from "../../../../core/services/purchase-order.service";
import { Router } from "@angular/router";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { PurchaseOrderVO, PurchaseOrderProductVO } from "../../../../core/model/purchase-order";
import { ProductService } from "../../../../core/services/product.service";
import { ProductVO } from "../../../../core/model/product";
import { QueryParams, ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../../../../core/model/result-vm";
import { BehaviorSubject, Observable } from "rxjs";
import { debounceTime, map, switchMap } from "rxjs/operators";
import { NzMessageService } from "ng-zorro-antd";
import { DateUtils, Objects } from "../../../../core/services/util.service";
import { HttpErrorResponse } from "@angular/common/http";
import { SupplierVO } from "../../../../core/model/supplier";
import { SupplierService } from "../../../../core/services/supplier.service";

@Component({
  selector: 'purchase-order-add',
  templateUrl: './purchase-order-add.component.html',
  styleUrls: [ './purchase-order-add.component.less' ]
})
export class PurchaseOrderAddComponent implements ClosableTab, OnInit {

  purchaseOrderForm: FormGroup;
  products: PurchaseOrderProductVO[] = [];
  editCache: {
    _id?: number,
    data?: TempPurchaseOrderProductInfoVO,
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
  productCountIndex: number = 0;
  searchProducts: ProductVO[];
  searchProductsChange$: BehaviorSubject<string> = new BehaviorSubject('');
  isProductLoading: boolean = false;
  searchSuppliers: SupplierVO[];
  searchSuppliersChange$: BehaviorSubject<string> = new BehaviorSubject('');
  isSupplierLoading: boolean = false;

  isSaving: boolean = false;

  cashFormatter: any = (value: number) => {
    if (Objects.valid(value)) {
      return `¥ ${value}`;
    } else {
      return `¥`;
    }
  };
  disabledDate: any = (current: Date): boolean => {
    return DateUtils.compare(current, new Date()) > 0;
  };

  constructor(
    private closeTab: TabService,
    private purchaseOrder: PurchaseOrderService,
    private supplier: SupplierService,
    private router: Router,
    private fb: FormBuilder,
    private product: ProductService,
    private message: NzMessageService
  ) {

  }

  ngOnInit(): void {
    Object.assign(this.editCache, this.defaultEditCache);
    this.purchaseOrderForm = this.fb.group({
      supplierId: [ null, Validators.required ],
      description: [ null ],
      cash: [ null, Validators.required ],
      salesman: [ null ],
      doneAt: [ null, Validators.required ]
    });
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
    const productOptionList$: Observable<ProductVO[]> = this.searchProductsChange$
      .asObservable()
      .pipe(debounceTime(500))
      .pipe(switchMap(getProducts));
    productOptionList$.subscribe((data: ProductVO[]) => {
      this.searchProducts = data;
      this.isProductLoading = false;
    });
    const getSuppliers: any = (name: string) => {
      const t: Observable<ResultVO<TableResultVO<SupplierVO>>>
        = <Observable<ResultVO<TableResultVO<SupplierVO>>>>this.supplier
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
    const supplierOptionList$: Observable<SupplierVO[]> = this.searchSuppliersChange$
      .asObservable()
      .pipe(debounceTime(500))
      .pipe(switchMap(getSuppliers));
    supplierOptionList$.subscribe((data: SupplierVO[]) => {
      this.searchSuppliers = data;
      this.isSupplierLoading = false;
    });
  }

  saveOrder(): void {
    if (!this.purchaseOrderForm.valid) {
      return;
    }
    let formData: any = this.purchaseOrderForm.getRawValue();
    let purchaseOrderAdd: PurchaseOrderVO = {
      supplierId: formData.supplierId,
      description: formData.description,
      cash: formData.cash,
      salesman: formData.salesman,
      doneAt: DateUtils.format(formData.doneAt),
      products: this.products
    };

    this.isSaving = true;
    this.purchaseOrder.save(purchaseOrderAdd)
      .pipe(debounceTime(3000))
      .subscribe((res: ResultVO<any>) => {
        console.log(res);
        this.message.success('添加成功!');
        this.isSaving = false;
        // TODO: 跳转回列表页面
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  addRow(): void {
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存商品列表的更改!');
      return;
    }
    let item: PurchaseOrderProductVO = {
      id: 0,
      productId: 0,
      name: '',
      quantity: 0,
      weight: 0,
      price: 0,
      priceType: 1,
      cash: 0
    };
    item['_id'] = this.productCountIndex ++;
    console.log(item);
    this.products = [
      item,
      ...this.products
    ];
    this.startEdit(item['_id'], true);
  }

  onProductSearch(value: string): void {
    this.isProductLoading = true;
    this.searchProductsChange$.next(value);
  }

  onChangeProductSelected(event: TempProductVO): void {
    this.editCache.data.productId = event.id;
    this.editCache.data.name = event.name;
  }

  onSupplierSearch(value: string): void {
    this.isSupplierLoading = true;
    this.searchSuppliersChange$.next(value);
  }

  startEdit(_id: number, isAdd?: boolean): void {
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存商品列表的更改!');
      return;
    }
    const index = this.products.findIndex(item => item['_id'] === _id);
    if (index === -1) {
      this.message.error('没有该商品条目!');
      return;
    }
    Object.assign(this.editCache, this.defaultEditCache);
    this.editCache._id = _id;
    // 一层深拷贝
    this.editCache.data = {};
    Object.assign(this.editCache.data, this.products[index]);
    this.editCache.currentProduct = {
      id: this.products[index].productId,
      name: this.products[index].name
    };
    this.editCache.product = this.editCache.currentProduct;
    if (Objects.valid(isAdd) && isAdd) {
      this.editCache.isAdd = isAdd;
    } else {
      this.editCache.isAdd = false;
    }
  }

  cancelEdit(_id: number): void {
    if (Objects.valid(this.editCache.isAdd) && this.editCache.isAdd) {
      this.products = this.products.filter(item => item['_id'] !== _id);
    }
    Object.assign(this.editCache, this.defaultEditCache);
  }

  saveEdit(_id: number): void {
    if (_id !== this.editCache._id) {
      return;
    }
    const index = this.products.findIndex(item => item['_id'] === _id);
    Object.assign(this.products[index], this.editCache.data);
    Object.assign(this.editCache, this.defaultEditCache);
  }

  confirmDelete(_id: number): void {
    this.products = this.products.filter(item => item['_id'] !== _id);
  }

  tabClose(): void {
  }

}

interface TempProductVO {

  id: number;
  name: string;
  [key: string]: any;

}

interface TempPurchaseOrderProductInfoVO {

  id?: number;
  productId?: number,
  name?: string;
  quantity?: number;
  weight?: number;
  price?: number;
  priceType?: number;
  cash?: number;

}
