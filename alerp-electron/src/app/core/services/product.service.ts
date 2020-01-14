import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { QueryParams, ResultVO, TableQueryParams, TableResultVO } from "../model/result-vm";
import { Observable, of } from "rxjs";
import { ProductVO } from "../model/product";
import {CustomerVO} from "../model/customer";
import {AppConfig} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  constructor(
    private http: HttpClient
  ) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<ProductVO>>>{
    return this.http.get<ResultVO<TableResultVO<ProductVO>>>(`${AppConfig.BASE_URL}/api/product/list`,
      {
        params: queryParams,
        withCredentials: true
      });
  };

  public updateOrAddProduct(queryParams: ProductVO): Observable<ResultVO<any>>{
    console.log(queryParams);
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/product`, queryParams, {
      withCredentials: true
    });
  }

  public deleteProduct(productId: number): Observable<ResultVO<any>>{
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/product/delete/${productId}`);
  }

}
