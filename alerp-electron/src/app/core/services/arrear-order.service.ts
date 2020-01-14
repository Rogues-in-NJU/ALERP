import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import {QueryParams, ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../model/result-vm";
import {PurchaseOrderPaymentRecordVO, PurchaseOrderVO} from "../model/purchase-order";
import {ShippingOrderInfoVO} from "../model/shipping-order";
import {ArrearOrderInfoVO, ArrearOrderReceiptRecordVO, ArrearStatisticsVO} from "../model/arrear-order";
import {AppConfig} from "../../../environments/environment";
import {CustomerVO} from "../model/customer";

@Injectable({
  providedIn: 'root'
})
export class ArrearOrderService {

  constructor(
    private http: HttpClient
  ) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<ArrearOrderInfoVO>>> {
    return this.http.get<ResultVO<TableResultVO<ArrearOrderInfoVO>>>(`${AppConfig.BASE_URL}/api/arrear-order/list`, {
      params: queryParams,
      withCredentials: true
    });
  }

  public find(id: number): Observable<ResultVO<ArrearOrderInfoVO>> {
    return this.http.get<ResultVO<ArrearOrderInfoVO>>(
      `${AppConfig.BASE_URL}/api/arrear-order/${id}`,{
        withCredentials: true
      });

  }

  public saveReceiptRecord(receiptRecord: ArrearOrderReceiptRecordVO): Observable<ResultVO<any>> {
    console.log(receiptRecord);
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/arrear-order/receipt-record/`, receiptRecord,
      {
        withCredentials: true
      });

  }

  public getArrearStatistics() : Observable<ResultVO<ArrearStatisticsVO>>{
    return this.http.get<ResultVO<ArrearStatisticsVO>>(`${AppConfig.BASE_URL}/api/overdue-warning`,{
      withCredentials: true
    });

  }

  public changeInvoiceNumber(params: QueryParams): Observable<ResultVO<any>> {
    console.log(params);
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/arrear-order/invoice-number`, params,
      {
        withCredentials: true
      });
  }
  public changeDueDate(params: QueryParams): Observable<ResultVO<any>> {
    console.log(params);
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/arrear-order/due-date`, params,
      {
        withCredentials: true
      });
  }

  public deleteReceiptRecord(id: number): Observable<ResultVO<any>> {

    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/arrear-order/receipt-record/delete/${id}`, {
      withCredentials: true
    });
  }


}
