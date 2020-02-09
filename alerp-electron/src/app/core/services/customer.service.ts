import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { ResultVO, TableQueryParams, TableResultVO } from "../model/result-vm";
import { CustomerVO } from "../model/customer";
import { AppConfig } from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  constructor(
    private http: HttpClient
  ) {

  }

  public find(id: number): Observable<ResultVO<CustomerVO>> {
    return this.http.get<ResultVO<CustomerVO>>(`${AppConfig.BASE_URL}/api/customer/${id}`);

  }

  public findAll(
    queryParams: TableQueryParams
  ): Observable<ResultVO<TableResultVO<CustomerVO>>> {
    return this.http.get<ResultVO<TableResultVO<CustomerVO>>>(`${AppConfig.BASE_URL}/api/customer/list`, {
      params: queryParams,
      withCredentials: true
    });
  }

  public findAllMonth(
    queryParams: TableQueryParams
  ): Observable<ResultVO<TableResultVO<CustomerVO>>> {
    return this.http.get<ResultVO<TableResultVO<CustomerVO>>>(`${AppConfig.BASE_URL}/api/customer/monthList`, {
      params: queryParams,
      withCredentials: true
    });
  }

  public save(customer: CustomerVO): Observable<ResultVO<any>> {
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/customer`, customer);
  }

  public delete(id: number): Observable<ResultVO<any>> {
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/customer/delete/${id}`);
  }

}
