import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { QueryParams, ResultVO, TableQueryParams, TableResultVO } from "../model/result-vm";
import { Observable, of } from "rxjs";
import { ProductVO } from "../model/product";

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  constructor(
    private http: HttpClient
  ) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<ProductVO>>>;

  public findAll(queryParams: QueryParams): Observable<ResultVO<ProductVO[]>>;

  public findAll(
    queryParams: QueryParams | TableQueryParams
  ): Observable<ResultVO<ProductVO[]>>
    | Observable<ResultVO<TableResultVO<ProductVO>>> {
    if (queryParams instanceof TableQueryParams) {
      return of({
        code: 200,
        message: '',
        data: {
          totalPages: 1,
          pageIndex: 1,
          pageSize: 10,
          result:  [{
            id: 1,
            name: 'XX商品',
            shorthand: 'XX',
            type: 1,
            density: 1,
            specification: '2*2*2'
          },
            {
              id: 2,
              name: 'YY商品',
              shorthand: 'XX',
              type: 1,
              density: 1,
              specification: '2*2*2'
            }
          ]
        }
      });
    } else {
      return of({
        code: 200,
        message: '',
        data: [{
          id: 1,
          name: 'XX商品',
          shorthand: 'XX',
          type: 1,
          density: 1,
          specification: '2*2*2'
        }, {
          id: 2,
          name: 'YY商品',
          shorthand: 'XX',
          type: 1,
          density: 1,
          specification: '2*2*2'
        }]
      });
    }
  }

  public updateOrAddProduct(queryParams: ProductVO): ResultVO<any>{
    return {
      code: 200,
      message: 'success',
    }
  }

  public deleteProduct(productId: string): ResultVO<any>{
    return {
      code: 400,
      message: '网络错误',
    }
  }

}
