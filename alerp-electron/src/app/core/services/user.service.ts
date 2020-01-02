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

  public login(phoneNumber: string, password: string, city: number): Observable<ResultVO<any>> {
    return this.http.post<ResultVO<any>>('', {
      phone_number: phoneNumber,
      password: password,
      city: city
    });
  }

  public hasActionAuth(route: string, action: number): Observable<ResultVO<boolean>> {
    return of(null);
  }

}
