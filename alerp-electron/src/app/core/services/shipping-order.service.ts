import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import {ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../model/result-vm";
import {ShippingOrderInfoVO} from "../model/shipping-order";
import {ProcessingOrderProductVO, ProcessingOrderVO} from "../model/processing-order";
import {AppConfig} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ShippingOrderService {

  constructor(
    private http: HttpClient
  ) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<ShippingOrderInfoVO>>> {

    return this.http.get<ResultVO<TableResultVO<ShippingOrderInfoVO>>>(`${AppConfig.BASE_URL}/api/shipping-order/list`, {
      params: queryParams,
      withCredentials: true
    });
  }

  public find(id: string): Observable<ResultVO<ShippingOrderInfoVO>> {
    return of({
      code: ResultCode.SUCCESS.code,
      message: '',
      data: {
        id: 1,
        code: '200201912120001',
        customerId: 1,
        customerName: '小浣熊公司',
        arrearOrderId: null,
        arrearOrderCode: '300201912120001',
        status: 1,
        cash: 165,
        floatingCash: -5,
        receivableCash: 160,
        createdAt: '2019-12-17 12:00',
        createdByName: '于海强',

        processingOrderCodes: ['100201912120001', "100201912120002"],

        products: [{
          id: 1,
          productId: 1,
          productName: '铝棒',
          shippingOrderCode: '00001000',
          processingOrderCode: '100201912120001',
          type: 1,
          density: 1.00,
          specification: '1*1*1',
          quantity: 1,
          expectedWeight: 1,
          price: 15,
          priceType: 1,
          weight: 1.25,
          cash: 15,
        },{
          id: 2,
          productId: 2,
          productName: '铝板',
          shippingOrderCode: '00001000',
          processingOrderCode: '100201912120002',
          type: 2,
          density: 1.00,
          specification: '12*12*12',
          quantity: 1,
          expectedWeight: 1.1,
          price: 150,
          priceType: 0,
          weight: 1,
          cash: 150,
        }]
      }
    });
  }

  public save(info: ProcessingOrderVO): Observable<ResultVO<any>> {
    return of(null);
  }

  public abandon(id: number): Observable<ResultVO<any>> {
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/shipping-order/delete/${id}`, {
      withCredentials: true
    });
  }

  public saveProduct(product: ShippingOrderInfoVO): Observable<ResultVO<any>> {
    return of({
      code: ResultCode.SUCCESS.code,
      message: '',
      data: null
    });
  }

  public deleteProduct(id: number): Observable<ResultVO<any>> {
    return of({
      code: ResultCode.SUCCESS.code,
      message: '',
      data: null
    });
  }

  //根据加工单ids生成新的出货单
  //不对，前端根据加工单生成界面，确认后生成出货单
}
