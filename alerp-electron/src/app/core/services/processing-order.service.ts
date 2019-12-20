import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../model/result-vm";
import { ProcessingOrderProductVO, ProcessingOrderVO } from "../model/processing-order";

@Injectable({
  providedIn: 'root'
})
export class ProcessingOrderService {

  constructor(
    private http: HttpClient
  ) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<ProcessingOrderVO>>> {
    return of({
      code: ResultCode.SUCCESS.code,
      message: '',
      data: {
        totalPages: 1,
        pageIndex: 1,
        pageSize: 10,
        result: [{
          id: 1,
          code: '00001000',
          customerId: 1,
          customerName: 'XXX',
          shippingOrderId: null,
          shippingOrderName: null,
          salesman: '',
          status: 1,
          createdAt: '2019-12-17 12:00'
        }]
      }
    });
  }

  public find(id: number): Observable<ResultVO<ProcessingOrderVO>> {
    return of({
      code: ResultCode.SUCCESS.code,
      message: '',
      data: {
        id: 1,
        code: '00001000',
        customerId: 1,
        customerName: 'XXX',
        shippingOrderId: null,
        shippingOrderCode: '11111111',
        salesman: '',
        status: 1,
        createdAt: '2019-12-17 12:00',
        createdByName: 'XXX',

        products: [{
          id: 1,
          productId: 1,
          productName: '铝棒',
          type: 1,
          density: 1.00,
          productSpecification: '1*1*1',
          specification: '1*1*1',
          quantity: 1,
          expectedWeight: 1
        }]
      }
    });
  }

  public save(info: ProcessingOrderVO): Observable<ResultVO<any>> {
    return of(null);
  }

  public abandon(id: number): Observable<ResultVO<any>> {
    return of(null);
  }

  public saveProduct(product: ProcessingOrderProductVO): Observable<ResultVO<any>> {
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

}
