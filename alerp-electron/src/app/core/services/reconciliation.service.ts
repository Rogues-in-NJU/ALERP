import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import {QueryParams, ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../model/result-vm";
import {ShippingOrderInfoVO} from "../model/shipping-order";
import {ProcessingOrderProductVO, ProcessingOrderVO} from "../model/processing-order";
import {AppConfig} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ReconciliationService {

  constructor(
    private http: HttpClient
  ) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<ShippingOrderInfoVO>>> {

    return this.http.get<ResultVO<TableResultVO<ShippingOrderInfoVO>>>(`${AppConfig.BASE_URL}/api/shipping-order/reconciliation`, {
      params: queryParams,
      withCredentials: true
    });
  }

  public changeAllInvoiceNumber(params: QueryParams): Observable<ResultVO<any>> {
    console.log(params);
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/arrear-order/invoice-number-together`, params,
      {
        withCredentials: true
      });
  }

  public reconciliationAll(params: QueryParams): Observable<ResultVO<any>> {
    console.log(params);
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/shipping-order/print-list`, params,
      {
        withCredentials: true
      });
  }

}
