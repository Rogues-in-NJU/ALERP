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

  public findAll(
    queryParams: TableQueryParams
  ): Observable<ResultVO<TableResultVO<SupplierVO>>> {
    return this.http.get<ResultVO<TableResultVO<SupplierVO>>>(`${AppConfig.BASE_URL}/api/supplier/list`, {
      params: queryParams,
      withCredentials: true
    });
  }

  public save(supplier: SupplierVO): Observable<ResultVO<any>> {
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
