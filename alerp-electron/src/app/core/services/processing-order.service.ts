import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../model/result-vm";
import { ProcessingOrderProductVO, ProcessingOrderVO } from "../model/processing-order";
import { AppConfig } from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ProcessingOrderService {

  constructor(
    private http: HttpClient
  ) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<ProcessingOrderVO>>> {
    return this.http.get<ResultVO<TableResultVO<ProcessingOrderVO>>>(`${AppConfig.BASE_URL}/api/process-order/list`, {
      params: queryParams,
      withCredentials: true
    });

  }

  public find(id: number): Observable<ResultVO<ProcessingOrderVO>> {
    return this.http.get<ResultVO<ProcessingOrderVO>>(`${AppConfig.BASE_URL}/api/process-order/${id}`, {
      withCredentials: true
    });

  }

  public save(info: ProcessingOrderVO): Observable<ResultVO<any>> {
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/process-order`, info, {
      withCredentials: true
    });
    // return of(null);
  }

  public abandon(id: number): Observable<ResultVO<any>> {
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/process-order/delete/${id}`, {
      withCredentials: true
    });
    // return of(null);
  }

  public saveProduct(product: ProcessingOrderProductVO): Observable<ResultVO<any>> {
    console.log(product);
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/process-order/product`, product, {
      withCredentials: true
    });
    // return of({
    //   code: ResultCode.SUCCESS.code,
    //   message: '',
    //   data: null
    // });
  }

  public deleteProduct(id: number): Observable<ResultVO<any>> {
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/process-order/product/delete/${id}`, {
      withCredentials: true
    });
    // return of({
    //   code: ResultCode.SUCCESS.code,
    //   message: '',
    //   data: null
    // });
  }

}
