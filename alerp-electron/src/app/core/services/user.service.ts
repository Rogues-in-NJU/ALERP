import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { ResultVO } from "../model/result-vm";
import { LoginResultVO, PassportVO, UserInfoVO } from "../model/user";
import { AppConfig } from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(
    private http: HttpClient
  ) {
  }

  public find(id: string): Observable<ResultVO<UserInfoVO>> {
    return of(null);
  }

  public login(loginVO: PassportVO): Observable<ResultVO<LoginResultVO>> {
    return this.http.post<ResultVO<LoginResultVO>>(`${AppConfig.BASE_URL}/api/user/login`, loginVO, {
      withCredentials: true
    });
  }

  public logout(): Observable<ResultVO<any>> {
    return this.http.get<ResultVO<any>>(`${AppConfig.BASE_URL}/api/user/logout`, {
      withCredentials: true
    });
  }

  public hasActionAuth(route: string, action: number): Observable<ResultVO<boolean>> {
    return of(null);
  }

  public isSessionValidate(): Observable<ResultVO<boolean>> {
    return this.http.get<ResultVO<boolean>>(`${AppConfig.BASE_URL}/api/user/sessionValidation`, {
      withCredentials: true
    });
  }

}
