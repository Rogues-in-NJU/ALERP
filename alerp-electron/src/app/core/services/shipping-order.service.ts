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

  public find(id: number): Observable<ResultVO<ShippingOrderInfoVO>> {
    return this.http.get<ResultVO<ShippingOrderInfoVO>>(`${AppConfig.BASE_URL}/api/shipping-order/${id}`, {
      withCredentials: true
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
