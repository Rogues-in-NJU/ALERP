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
        params: queryParams
      });
    // return of({
      //   code: 10000,
      //   message: '',
      //   data: {
      //     totalPages: 1,
      //     pageIndex: 1,
      //     pageSize: 10,
      //     result:  [{
      //       id: 1,
      //       name: 'XX商品',
      //       shorthand: 'XX',
      //       type: 1,
      //       density: 1,
      //       specification: '2*2*2'
      //     },
      //       {
      //         id: 2,
      //         name: 'YY商品',
      //         shorthand: 'XX',
      //         type: 1,
      //         density: 1,
      //         specification: '2*2*2'
      //       }
      //     ]
      //   }
      // });
  };

  public updateOrAddProduct(queryParams: ProductVO): Observable<ResultVO<any>>{
    console.log(queryParams);
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/product/`, queryParams);
  }

  public deleteProduct(productId: string): ResultVO<any>{
    return {
      code: 400,
      message: '网络错误',
    }
  }

}
