import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {RefreshableTab} from "../../tab/tab.component";
import {UserManagementService} from "../../../../core/services/user-management.service";
import {ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../../../../core/model/result-vm";
import {HttpErrorResponse} from "@angular/common/http";
import {NzMessageService} from "ng-zorro-antd";
import {TabService} from "../../../../core/services/tab.service";
import {UserManagementInfoVO} from "../../../../core/model/user-management";
import {StringUtils} from "../../../../core/services/util.service";

@Component({
  selector: 'user-management-list',
  templateUrl: './user-management-list.component.html',
  styleUrls: ['./user-management-list.component.less']
})
export class UserManagementListComponent implements RefreshableTab, OnInit {

  isLoading: boolean = false;
  totalPages: number = 1;
  pageIndex: number = 1;
  pageSize: number = 10;

  name: string;

  userList: UserManagementInfoVO[] = [];

  constructor(private router: Router,
              private UserManagement: UserManagementService,
              private message: NzMessageService,
              private tab: TabService) {

  }

  ngOnInit(): void {
    this.search();
  }

  search(): void {
    const queryParams: TableQueryParams = {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    };
    if (!StringUtils.isEmpty(this.name)) {
      Object.assign(queryParams, {
        name: this.name
      });
      this.name = null;
    }
    this.UserManagement.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<UserManagementInfoVO>>) => {
        if (!res) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        const tableResult: TableResultVO<UserManagementInfoVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.userList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  confirmAbandon(id: string): void {
    console.log('confirm abandon: ' + id);
  }

  refresh(): void {
  }

}
