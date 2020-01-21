/**
 * Created by Administrator on 2019/12/18.
 */
import {Component, OnInit} from "@angular/core";
import {RefreshableTab} from "../../tab/tab.component";
import {TableQueryParams, ResultVO, TableResultVO, ResultCode} from "../../../../core/model/result-vm";
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

  query_name : string = null;
  query_type : number = null;

  products: ProductVO[] = [];
  searchProducts: ProductVO[];
  searchChange$: BehaviorSubject<string> = new BehaviorSubject('');

  editCache : {
    _id?: number,
    data?: ProductVO,
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
    this.search();
  }

  ngOnInit(): void {
      this.search();
  }

  search() : void{
    this.isLoading = true;

    const queryParams: TableQueryParams = {
      pageIndex : this.pageIndex,
      pageSize : this.pageSize,
      // name : this.query_name,
      // type : this.query_type
    };

    if(this.query_name != null){
      queryParams['name'] = this.query_name;
    }

    if(this.query_type != null){
      queryParams['type'] = this.query_type;
    }

    this.query_name = null;
    this.query_type = null;

    this.product.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ProductVO>>) =>{
        console.log(res);
        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }

        const tableResult: TableResultVO<ProductVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.products = tableResult.result;
        for(let product of this.products){
          product['_id'] = this.productCountIndex ++;
        }
    }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
    });

    Object.assign(this.editCache, this.defaultEditCache);

    this.isLoading = false;
  }

  addRow(){
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存商品列表的更改!');
      return;
    }
    let item: ProductVO = {
      id: null,
      name: '',
      shorthand: '',
      type: 0,
      density: 0.0027,
      specification: ''
    };
    item['_id'] = this.productCountIndex ++;
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

  confirmDelete(id: number){
    console.log(id);
    if (!Objects.valid(id)) {
      this.refresh();
      return;
    }
    this.product.deleteProduct(id)
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.message.success('删除成功!');
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
        this.refresh();
      }, () => {
        this.refresh();
      });}

  saveEdit(_id: number): void {
    if (_id !== this.editCache._id) {
      return;
    }
    if (!Objects.valid(this.editCache.data.name)) {
      this.message.warning('请录入商品名称!');
      return;
    }
    if (!Objects.valid(this.editCache.data.type)) {
      this.message.warning('请选择商品类型!');
      return;
    }
    this.isSaving = true;
    this.product.updateOrAddProduct(this.editCache.data)
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
        this.isSaving = false;
      }, () => {
        this.isSaving = false;
        this.refresh();
      });
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

  // onChangeProductSelected(event: TempProductVO): void {
  //   this.editCache.data.productId = event.id;
  //   this.editCache.data.name = event.name;
  // }

}

// interface TempProductInfoVO{
//   id?: number;
//   name?: string;
//   shorthand?: string;
//   type?: number;
//   density?: number;
//   specification?: string;
// }
