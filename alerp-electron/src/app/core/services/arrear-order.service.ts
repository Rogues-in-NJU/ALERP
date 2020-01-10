import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import {ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../model/result-vm";
import {PurchaseOrderPaymentRecordVO, PurchaseOrderVO} from "../model/purchase-order";
import {ShippingOrderInfoVO} from "../model/shipping-order";
import {ArrearOrderInfoVO, ArrearOrderReceiptRecordVO, ArrearStatisticsVO} from "../model/arrear-order";
import {AppConfig} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ArrearOrderService {

  constructor(
    private http: HttpClient
  ) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<ArrearOrderInfoVO>>> {
    // // prod
    // return this.http.post<ResultVO<TableResultVO<PurchaseOrderVO>>>(
    //   `${AppConfig.BASE_URL}/api/purchase-order/list`,
    //   queryParams
    // );
    // test
    return this.http.get<ResultVO<TableResultVO<ArrearOrderInfoVO>>>(`${AppConfig.BASE_URL}/api/arrear-order/list`, {
      params: queryParams,
      withCredentials: true
    });
  }

  public find(id: number): Observable<ResultVO<ArrearOrderInfoVO>> {
    // // prod
    // return this.http.get<ResultVO<PurchaseOrderVO>>(`${AppConfig.BASE_URL}/api/purchase-order/${_id}`);
    // test
    return of({
      code: 200,
      message: '',
      data: {
        id: 1,
        code: '300201912240001',
        shippingOrderId: 1,
        shippingOrderCode: '200201912230001',
        status: 1,
        invoiceNumber: '1234567890',
        customerId: 1,
        customerName: '尹子越',
        receivableCash: 10000,
        receivedCash: 2000,
        dueDate: '2019-12-31',
        overDue: false,
        createdBy: '于海强',

        receipts: [{
          id: 1,
          arrearOrderId: 1,
          status: 0,
          cash: 2000,
          salesman: '殷乾恩',
          description: '支付宝转账：652387423984720',
          doneAt: '2019-12-24 12:00'
        }]
      }
    })
  }

  public saveReceiptRecord(receiptRecord: ArrearOrderReceiptRecordVO): Observable<ResultVO<any>> {
    console.log(receiptRecord);
    return of({
      code: ResultCode.SUCCESS.code,
      message: '',
      data: null
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

}
