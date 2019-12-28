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
    if(queryParams.pageSize === 1000){
      return of({
        code: ResultCode.SUCCESS.code,
        message: '',
        data: {
          totalPages: 3,
          pageIndex: 1,
          pageSize: 2,
          result: [{
            id: 1,
            code: '100201912260001',
            customerId: 1,
            customerName: '于海强',
            shippingOrderId: null,
            shippingOrderName: null,
            salesman: '',
            status: 1,
            createdAt: '2019-12-17 12:00'
          },{
            id: 2,
            code: '100201912260002',
            customerId: 2,
            customerName: '于海强2',
            shippingOrderId: null,
            shippingOrderName: null,
            salesman: '',
            status: 1,
            createdAt: '2019-12-17 12:00'
          },{
            id: 3,
            code: '100201912260003',
            customerId: 1,
            customerName: '于海强',
            shippingOrderId: null,
            shippingOrderName: null,
            salesman: '',
            status: 1,
            createdAt: '2019-12-17 12:00'
          },{
            id: 4,
            code: '100201912260004',
            customerId: 2,
            customerName: '于海强2',
            shippingOrderId: null,
            shippingOrderName: null,
            salesman: '',
            status: 1,
            createdAt: '2019-12-17 12:00'
          },{
            id: 5,
            code: '100201912260005',
            customerId: 1,
            customerName: '于海强',
            shippingOrderId: null,
            shippingOrderName: null,
            salesman: '',
            status: 1,
            createdAt: '2019-12-17 12:00'
          }]
        }
      });
    }

    if(queryParams.customerName === '于海强'){
      return of({
        code: ResultCode.SUCCESS.code,
        message: '',
        data: {
          totalPages: 2,
          pageIndex: 1,
          pageSize: 2,
          result: [{
            id: 1,
            code: '100201912260001',
            customerId: 1,
            customerName: '于海强',
            shippingOrderId: null,
            shippingOrderName: null,
            salesman: '',
            status: 1,
            createdAt: '2019-12-17 12:00'
          },{
            id: 3,
            code: '100201912260003',
            customerId: 1,
            customerName: '于海强',
            shippingOrderId: null,
            shippingOrderName: null,
            salesman: '',
            status: 1,
            createdAt: '2019-12-17 12:00'
          },{
            id: 5,
            code: '100201912260005',
            customerId: 1,
            customerName: '于海强',
            shippingOrderId: null,
            shippingOrderName: null,
            salesman: '',
            status: 1,
            createdAt: '2019-12-17 12:00'
          }]
        }
      });
    }

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
          processingOrderId: 1,
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
