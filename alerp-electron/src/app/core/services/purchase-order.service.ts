import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { ResultVO, TableQueryParams, TableResultVO } from "../model/result-vm";
import { PurchaseOrderPaymentRecordVO, PurchaseOrderVO } from "../model/purchase-order";
import { AppConfig } from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class PurchaseOrderService {

  constructor(
    private http: HttpClient
  ) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<PurchaseOrderVO>>> {
    // // prod
    return this.http.get<ResultVO<TableResultVO<PurchaseOrderVO>>>(`${AppConfig.BASE_URL}/api/purchase-order/list`, {
      params: queryParams,
      withCredentials: true
    });
  }

  public find(id: number): Observable<ResultVO<PurchaseOrderVO>> {
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/purchase-order/${id}`, {
      withCredentials: true
    });
  }

  public save(info: PurchaseOrderVO): Observable<ResultVO<any>> {
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/purchase-order`, info, {
      withCredentials: true
    });
  }

  public abandon(id: string): Observable<ResultVO<any>> {
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/purchase-order/abandon/${id}`, {
      withCredentials: true
    });
  }

  public savePaymentRecord(paymentRecord: PurchaseOrderPaymentRecordVO): Observable<ResultVO<any>> {
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/purchase-order/payment-record`, paymentRecord, {
      withCredentials: true
    });
  }

  public deletePaymentRecord(id: number): Observable<ResultVO<any>> {
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/purchase-order/payment-record/delete/${id}`, {
      withCredentials: true
    });
  }

}
