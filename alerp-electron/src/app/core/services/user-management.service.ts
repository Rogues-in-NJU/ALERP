import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {ResultVO, TableQueryParams, TableResultVO} from "../model/result-vm";
import {StringUtils} from "./util.service";
import {UserManagementInfoVO} from "../model/user-management";

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {

  constructor(private http: HttpClient) {

  }

  public findAll(queryParams: TableQueryParams): Observable<ResultVO<TableResultVO<UserManagementInfoVO>>> {
    // // prod
    // return this.http.post<ResultVO<TableResultVO<UserManagementInfoVO>>>(
    //   `${AppConfig.BASE_URL}/api/user-management/list`,
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
          name: '殷乾恩',
          phone_number: '13821378223',
          status: 1
        },{
          id: 2,
          name: '杨关',
          phone_number: '13822038223',
          status: 2
        }]
      }
    });
  }

  public find(id: string): Observable<ResultVO<UserManagementInfoVO>> {
    if (StringUtils.isEmpty(id)) {
      return of(null);
    }
    // // prod
    // return this.http.get<ResultVO<UserManagementInfoVO>>(`${AppConfig.BASE_URL}/api/user-management/${_id}`);
    // test
    return of({
      code: 200,
      message: '',
      data: {
        id: 1,
        name: '殷乾恩',
        phone_number: '13821378223',
        status: 1
      }
    });
  }

  public save(info: UserManagementInfoVO): Observable<ResultVO<any>> {
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
