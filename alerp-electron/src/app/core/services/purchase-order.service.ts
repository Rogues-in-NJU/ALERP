import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../model/result-vm";
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
    // test
    // return of({
    //   code: 200,
    //   message: '',
    //   data: {
    //     totalPages: 1,
    //     pageIndex: 1,
    //     pageSize: 10,
    //     result: [{
    //       id: 1,
    //       code: '00001000',
    //       description: '',
    //       supplierId: 1,
    //       supplierName: 'ZZZ公司',
    //       cash: 20000,
    //       salesman: '',
    //       doneAt: '2019-10-10 12:00',
    //       status: 1
    //     }]
    //   }
    // });
  }

  public find(id: number): Observable<ResultVO<PurchaseOrderVO>> {
    // // prod
    // return this.http.get<ResultVO<PurchaseOrderVO>>(`${AppConfig.BASE_URL}/api/purchase-order/${_id}`);
    // test
    // return of({
    //   code: 200,
    //   message: '',
    //   data: {
    //     id: 1,
    //     code: '00001000',
    //     supplierId: 1,
    //     supplierName: 'ZZZ公司',
    //     description: '',
    //     cash: 20000,
    //     status: 1,
    //     doneAt: '2018-04-24 18:00:00',
    //     salesman: '',
    //
    //     products: [{
    //       id: 1,
    //       productId: 1,
    //       name: '铝棒',
    //       quantity: 2,
    //       weight: 2,
    //       price: 200,
    //       priceType: 1,
    //       cash: 2000
    //     }],
    //
    //     paymentRecords: [{
    //       id: 1,
    //       purchaseOrderId: 1,
    //       cash: 100,
    //       description: '',
    //       salesman: '',
    //       doneAt: '2019-12-20 12:00'
    //     }]
    //   }
    // });
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/purchase-order/${id}`, {
      withCredentials: true
    });
  }

  public save(info: PurchaseOrderVO): Observable<ResultVO<any>> {
    // return of({
    //   code: 200,
    //   message: '',
    //   data: null
    // });
    console.log(JSON.stringify(info));
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/purchase-order`, info, {
      withCredentials: true
    });
  }

  public abandon(id: string): Observable<ResultVO<any>> {
    // return of({
    //   code: ResultCode.SUCCESS.code,
    //   message: '',
    //   data: null
    // });
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/purchase-order/abandon/${id}`, {
      withCredentials: true
    });
  }

  public savePaymentRecord(paymentRecord: PurchaseOrderPaymentRecordVO): Observable<ResultVO<any>> {
    // console.log(paymentRecord);
    // return of({
    //   code: ResultCode.SUCCESS.code,
    //   message: '',
    //   data: null
    // });
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/purchase-order/payment-record`, paymentRecord, {
      withCredentials: true
    });
  }

  public deletePaymentRecord(id: number): Observable<ResultVO<any>> {
    // return of({
    //   code: ResultCode.SUCCESS.code,
    //   message: '',
    //   data: null
    // });
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/purchase-order/payment-record/delete/${id}`, {
      withCredentials: true
    });
  }

}
