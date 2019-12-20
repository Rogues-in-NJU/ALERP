import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { ResultVO, TableQueryParams, TableResultVO } from "../model/result-vm";
import { StringUtils } from "./util.service";
import {ShippingOrderInfoVO} from "../model/shipping-order";

@Injectable({
  providedIn: 'root'
})
export class ShippingOrderService {

  constructor(
    private http: HttpClient
  ) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<ShippingOrderInfoVO>>> {

    return of({
      code: 200,
      message: '',
      data: {
        totalPages: 1,
        pageIndex: 1,
        pageSize: 10,
        result: [{
          id: 1,
          code: '00001000',
          customerId: 1,
          customerName: '于海强',
          arrearOrderId: null,
          state: 1,
          cash: 100.1,
          floatingCash: -0.1,
          receivableCash: 100,
          createdAt: '2019-10-10 12:00',
        }]
      }
    });

  }
}
