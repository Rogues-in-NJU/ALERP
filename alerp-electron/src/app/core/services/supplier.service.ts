import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { QueryParams, ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../model/result-vm";
import { SupplierVO } from "../model/supplier";
import { AppConfig } from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class SupplierService {

  constructor(
    private http: HttpClient
  ) {

  }

  public find(id: number): Observable<ResultVO<SupplierVO>> {
    return of({
      code: ResultCode.SUCCESS.code,
      message: '',
      data: {
        id: 1,
        name: 'XXX公司',
        description: ''
      }
    });
  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<SupplierVO>>>;

  public findAll(queryParams: QueryParams): Observable<ResultVO<SupplierVO[]>>;

  public findAll(
    queryParams: QueryParams | TableQueryParams
  ): Observable<ResultVO<SupplierVO[]>>
    | Observable<ResultVO<TableResultVO<SupplierVO>>> {
    if (queryParams instanceof TableQueryParams) {
      return this.http.get<ResultVO<TableResultVO<SupplierVO>>>(`${AppConfig.BASE_URL}/api/supplier/list`, {
        params: queryParams,
        withCredentials: true
      });
      // return of({
      //   code: ResultCode.SUCCESS.code,
      //   message: '',
      //   data: {
      //     totalPages: 1,
      //     pageIndex: 1,
      //     pageSize: 1,
      //     result: [{
      //       id: 1,
      //       name: 'XXX公司',
      //       description: ''
      //     }]
      //   }
      // });
    } else {
      return of({
        code: ResultCode.SUCCESS.code,
        message: '',
        data: [{
          id: 1,
          name: 'XXX公司',
          description: ''
        }]
      });
    }
  }

  public save(supplier: SupplierVO): Observable<ResultVO<any>> {
    // return of(null);
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/supplier`, supplier, {
      withCredentials: true
    });
  }

  public delete(id: number): Observable<ResultVO<any>> {
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/supplier/delete/${id}`, {
      withCredentials: true
    });
  }

}
