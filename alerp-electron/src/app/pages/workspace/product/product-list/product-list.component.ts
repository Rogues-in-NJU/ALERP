/**
 * Created by Administrator on 2019/12/18.
 */
import {Component, OnInit} from "@angular/core";
import {RefreshableTab} from "../../tab/tab.component";
import {TableQueryParams, ResultVO, TableResultVO} from "../../../../core/model/result-vm";
import {ProductService} from "../../../../core/services/product.service";
import {ProductVO} from "../../../../core/model/product";
import {HttpErrorResponse} from "@angular/common/http";
import {NzMessageService} from "ng-zorro-antd";
import {BehaviorSubject, Observable} from "rxjs";
import {Objects} from "../../../../core/services/util.service";
import { FormGroup } from "@angular/forms";

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.less']
})
export class ProductListComponent implements RefreshableTab, OnInit{

  productForm: FormGroup;
  totalPages : number = 1;
  pageIndex : number = 1;
  pageSize : number = 10;
  isLoading: boolean = false;
  isSaving: boolean = false;
  productCountIndex : number = 0;

  query_name : string ="";
  query_type : number = 0;

  products: ProductVO[] = [];
  searchProducts: ProductVO[];
  searchChange$: BehaviorSubject<string> = new BehaviorSubject('');

  editCache : {
    _id?: number,
    data?: TempProductInfoVO,
    isAdd?: boolean
  } = {};

  defaultEditCache: any  = {
    _id : null,
    data: null,
    isAdd: null
  };

  constructor(
    private product : ProductService,
    private message: NzMessageService,
    // private fb: FormBuilder,
  ){

  }
  refresh(): void {
  }

  ngOnInit(): void {
      this.search();
  }

  search() : void{
    this.isLoading = true;

    const queryParams: TableQueryParams = {
      pageIndex : this.pageIndex,
      pageSize : this.pageSize,
      name : this.query_name,
      type : this.query_type
    };

    this.product.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ProductVO>>) =>{
        if(!res){
          return;
        }
        if(res.code !== 200){
          return;
        }

        const tableResult: TableResultVO<ProductVO> = res.data;
        this.pageIndex = tableResult.pageIndex;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.products = tableResult.result;
        for(let product in this.products){
          product['_id'] = this.productCountIndex ++;
        }
    }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
    });

    Object.assign(this.editCache, this.defaultEditCache);

    // this.productForm = this.fb.group({
    //   name: [null, Validators.required],
    //   shortHand: [null],
    //   type: [null, Validators.required],
    //   density: [null, Validators.required],
    //   specification: [null]
    // });
    // const getProducts: any = (name: string) => {
    //   return this.product
    //     .findAll({})
    //     .pipe(
    //       map((res: ResultVO<ProductVO[]>) => res.data)
    //     )
    // };
    // const optionList$: Observable<ProductVO[]> = this.searchChange$
    //   .asObservable()
    //   .pipe(debounceTime(500))
    //   .pipe(switchMap(getProducts));
    // optionList$.subscribe((data: ProductVO[]) => {
    //   this.searchProducts = data;
    //   this.isLoading = false;
    // })
    this.isLoading = false;
  }

  addRow(){
    if (Objects.valid(this.editCache._id)) {
      console.log("!!!!!" + this.editCache._id);
      this.message.warning('请先保存商品列表的更改!');
      return;
    }
    let item: ProductVO = {
      id: 0,
      name: '',
      shorthand: '',
      type: 0,
      density: 0.0027,
      specification: ''
    };
    item['_id'] = this.productCountIndex ++;
    console.log(item);
    this.products = [
      item,
      ...this.products
    ];
    this.startEdit(item['_id'], true);
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
    // this.editCache.currentProduct = {
    //   id: this.products[index].productId,
    //   name: this.products[index].name
    // };
    // this.editCache.product = this.editCache.currentProduct;
    if (Objects.valid(isAdd) && isAdd) {
      this.editCache.isAdd = isAdd;
    } else {
      this.editCache.isAdd = false;
    }
  }

  confirmDelete(_id: number){
    this.products = this.products.filter(item => item['_id'] !== _id);
  }

  saveEdit(_id: number): void {
    if (_id !== this.editCache._id) {
      return;
    }
    const index = this.products.findIndex(item => item['_id'] === _id);
    Object.assign(this.products[index], this.editCache.data);
    Object.assign(this.editCache, this.defaultEditCache);
  }

  cancelEdit(_id: number){
    if (Objects.valid(this.editCache.isAdd) && this.editCache.isAdd) {
      this.products = this.products.filter(item => item['_id'] !== _id);
    }
    Object.assign(this.editCache, this.defaultEditCache);
  }

  onSearch(value: string): void {
    this.isLoading = true;
    this.searchChange$.next(value);
  }

  // onChangeSelected(event: TempProductVO): void {
  //   this.editCache.data.productId = event.id;
  //   this.editCache.data.name = event.name;
  // }

}

interface TempProductInfoVO{
  id?: number;
  name?: string;
  shorthand?: string;
  type?: number;
  density?: number;
  specification?: string;
}
