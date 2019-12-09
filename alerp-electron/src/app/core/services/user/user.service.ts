import { Injectable } from "@angular/core";
import { HttpClient, HttpRequest } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { ResultVM } from "../../model/result-vm";
import { StringUtils } from "../util/util.service";
import { AppConfig } from "../../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(
    private http: HttpClient
  ) {
  }

  getInfo(): Observable<ResultVM<UserInfo>> {
    const token: string = localStorage.getItem("t");
    if (StringUtils.isEmpty(token)) {
      return of<ResultVM<UserInfo>>(null);
    } else {
      return this.http.get<ResultVM<UserInfo>>(`${AppConfig.BASE_URL}`);
    }
  }

}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private http: HttpClient
  ) {
  }

  public login(username: string, password: string): Observable<ResultVM<any>> {
    return this.http.post<ResultVM<any>>('', {
      username: username,
      password: password
    });
  }

}

export interface UserInfo {
  readonly username: string

  constructor(init?: {
    username?: string
  });

}
