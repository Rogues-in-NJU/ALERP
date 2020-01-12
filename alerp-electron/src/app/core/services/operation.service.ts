import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {ResultVO, TableQueryParams, TableResultVO} from "../model/result-vm";
import {StringUtils} from "./util.service";
import {OperationInfoVO} from "../model/operation";
import {AppConfig} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class OperationService {

  constructor(private http: HttpClient) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<OperationInfoVO>>> {
    console.log(queryParams);
    return this.http.get<ResultVO<TableResultVO<OperationInfoVO>>>(
      `${AppConfig.BASE_URL}/api/user/operation-log/list`, {
        params: queryParams,
        withCredentials: true
      }
    );
  }


}
