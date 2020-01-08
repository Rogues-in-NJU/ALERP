import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {ResultVO, TableQueryParams, TableResultVO} from "../model/result-vm";
import { AppConfig } from "../../../environments/environment";
import {ExpenseInfoVO} from "../model/expense";

@Injectable({
  providedIn: 'root'
})
export class ExpenseService {

  constructor(private http: HttpClient) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<ExpenseInfoVO>>> {
    return this.http.get<ResultVO<TableResultVO<ExpenseInfoVO>>>(
      `${AppConfig.BASE_URL}/api/expense/list`,{
        params: queryParams,
        withCredentials: true
      }
    );
  }

  public save(info: ExpenseInfoVO): Observable<ResultVO<any>> {
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/expense`, info, {
      withCredentials: true
    });
  }

  public abandon(id: string): Observable<ResultVO<any>> {
    return of(null);
  }

}
