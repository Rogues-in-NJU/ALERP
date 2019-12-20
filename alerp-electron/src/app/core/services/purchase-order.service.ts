import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { ResultVO, TableQueryParams, TableResultVO } from "../model/result-vm";
import { PurchaseOrderVO } from "../model/purchase-order";
import { StringUtils } from "./util.service";

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
    // return this.http.post<ResultVO<TableResultVO<PurchaseOrderVO>>>(
    //   `${AppConfig.BASE_URL}/api/purchase-order/list`,
    //   queryParams
    // );
    // test
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
          description: '',
          purchasingCompany: '',
          cash: 20000,
          salesman: '',
          doneAt: '2019-10-10 12:00',
          status: 1
        }]
      }
    });
  }

  public find(id: number): Observable<ResultVO<PurchaseOrderVO>> {
    // // prod
    // return this.http.get<ResultVO<PurchaseOrderVO>>(`${AppConfig.BASE_URL}/api/purchase-order/${_id}`);
    // test
    return of({
      code: 200,
      message: '',
      data: {
        id: 1,
        code: '00001000',
        purchasingCompany: 'XXX公司',
        description: '',
        cash: 20000,
        status: 1,
        doneAt: '2018-04-24 18:00:00',
        salesman: '',

        products: [{
          id: 1,
          productId: 1,
          name: '铝棒',
          quantity: 2,
          weight: 2,
          price: 200,
          priceType: 1,
          cash: 2000
        }]
      }
    });
  }

  public save(info: PurchaseOrderVO): Observable<ResultVO<any>> {
    return of({
      code: 200,
      message: '',
      data: null
    });
  }

  public abandon(id: string): Observable<ResultVO<any>> {
    return of(null);
  }

}
