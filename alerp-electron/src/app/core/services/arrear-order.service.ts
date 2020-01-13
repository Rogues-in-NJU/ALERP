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
    return this.http.get<ResultVO<ArrearOrderInfoVO>>(`${AppConfig.BASE_URL}/api/arrear-order/${id}`);

  }

  public saveReceiptRecord(receiptRecord: ArrearOrderReceiptRecordVO): Observable<ResultVO<any>> {
    console.log(receiptRecord);
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/arrear-order/receipt-record/`, receiptRecord,
      {
        withCredentials: true
      });

  }

  public getArrearStatistics() : Observable<ResultVO<ArrearStatisticsVO>>{
    return of({
      code: ResultCode.SUCCESS.code,
      message: '',
      data: {
        customers: [
          {
            customerId: 1,
            customerName: '于海强1',
            overdues: [{
                month: '七月',
                cash: 10000,
              },{
                month: '八月',
                cash: 10000,
              },{
                month: '九月',
                cash: 10000,
            }],
            total: 30000
          },{
            customerId: 2,
            customerName: '于海强2',
            overdues: [{
              month: '七月',
              cash: 10000,
            },{
              month: '八月',
              cash: 10000,
            },{
              month: '九月',
              cash: 10000,
            }],
            total: 30000
          },{
            customerId: 3,
            customerName: '于海强3',
            overdues: [{
              month: '七月',
              cash: 10000,
            },{
              month: '八月',
              cash: 10000,
            },{
              month: '九月',
              cash: 10000,
            }],
            total: 30000
          }
        ],
        statistics:{
          overdues: [{
            month: '七月',
            cash: 30000,
          },{
            month: '八月',
            cash: 30000,
          },{
            month: '九月',
            cash: 30000,
          }],
          total: 90000
        }
      }
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


}
