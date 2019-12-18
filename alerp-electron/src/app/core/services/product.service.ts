import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import {ResultVO, TableQueryParams, TableResultVO} from "../model/result-vm";
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

  findAll(queryParams: object): Observable<ResultVO<ProductVO[]>> {
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
    })
  }

  findAllByPage(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<ProductVO>>> {
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
      })
  }

}
