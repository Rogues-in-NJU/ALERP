import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {RefreshableTab} from "../../tab/tab.component";
import {UserManagementService} from "../../../../core/services/user-management.service";
import {ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../../../../core/model/result-vm";
import {HttpErrorResponse} from "@angular/common/http";
import {NzMessageService} from "ng-zorro-antd";
import {TabService, RefreshTabEvent} from "../../../../core/services/tab.service";
import {UserManagementInfoVO} from "../../../../core/model/user-management";
import {StringUtils, Objects} from "../../../../core/services/util.service";

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
    this.tab.refreshEvent.subscribe((event: RefreshTabEvent) => {
      if (Objects.valid(event) && event.url === '/workspace/user-management/list') {
        this.refresh();
      }
    });
    this.refresh();
  }

  resetQueryParams(): void{
    this.name = null;
    this.refresh();
  }

  resetIndex(): void {
    this.pageSize = 10;
    this.pageIndex = 1;
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
    }
    this.UserManagement.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<UserManagementInfoVO>>) => {
        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
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
    this.UserManagement.abandon(id)
      .subscribe((res: ResultVO<any>) => {
        console.log(res);
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      }, () => {
      });
    this.refresh();
  }

  resetPassword(id: string): void {
    this.UserManagement.resetPassword(id)
      .subscribe((res: ResultVO<any>) => {
        console.log(res);
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      }, () => {
        this.message.success("重置密码成功");
      });
    this.refresh();
  }

  refresh(): void {
    this.search();
  }

}
