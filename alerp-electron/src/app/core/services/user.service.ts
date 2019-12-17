import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { ResultVO } from "../model/result-vm";
import { UserInfoVO } from "../model/user";

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

}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private http: HttpClient
  ) {
  }

  public login(username: string, password: string): Observable<ResultVO<any>> {
    return this.http.post<ResultVO<any>>('', {
      username: username,
      password: password
    });
  }

  public hasActionAuth(route: string, action: number): Observable<ResultVO<boolean>> {
    return of(null);
  }

}
