import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {ResultVO, TableQueryParams, TableResultVO} from "../model/result-vm";
import {StringUtils} from "./util.service";
import {UserManagementInfoVO} from "../model/user-management";
import {AppConfig} from "../../../environments/environment";
import {PasswordInfoVO} from "../model/password";
import {AuthVO} from "../model/auth";

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {

  constructor(private http: HttpClient) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<UserManagementInfoVO>>> {
    return this.http.get<ResultVO<TableResultVO<UserManagementInfoVO>>>(
      `${AppConfig.BASE_URL}/api/user/list`, {
        params: queryParams,
        withCredentials: true
      }
    );
  }

  public getAuthList(): Observable<ResultVO<TableResultVO<AuthVO>>> {
    return this.http.get<ResultVO<TableResultVO<AuthVO>>>(
      `${AppConfig.BASE_URL}/api/auth/list`, {
        withCredentials: true
      }
    );
  }

  public find(id: string): Observable<ResultVO<UserManagementInfoVO>> {
    if (StringUtils.isEmpty(id)) {
      return of(null);
    }
    return this.http.get<ResultVO<UserManagementInfoVO>>(`${AppConfig.BASE_URL}/api/user/${id}`);
  }

  public findSelf(): Observable<ResultVO<UserManagementInfoVO>> {
    return this.http.get<ResultVO<UserManagementInfoVO>>(`${AppConfig.BASE_URL}/api/user/self`);
  }

  public save(info: UserManagementInfoVO): Observable<ResultVO<any>> {
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/user`, info, {
      withCredentials: true
    });
  }

  public update(info: UserManagementInfoVO): Observable<ResultVO<any>> {
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/user/update`, info, {
      withCredentials: true
    });
  }

  public updatePassword(info: PasswordInfoVO): Observable<ResultVO<any>> {
    return this.http.post<ResultVO<any>>(`${AppConfig.BASE_URL}/api/user/updatePassword`, info, {
      withCredentials: true
    });
  }

  public abandon(id: string): Observable<ResultVO<any>> {
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/user/delete/${id}`, {
      withCredentials: true
    });
  }

  public resetPassword(id: string): Observable<ResultVO<any>> {
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/user/reloadPassword/${id}`, {
      withCredentials: true
    });
  }

}
