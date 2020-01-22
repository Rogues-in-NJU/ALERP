import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {RefreshableTab} from "../../tab/tab.component";
import {ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../../../../core/model/result-vm";
import {HttpErrorResponse} from "@angular/common/http";
import {NzMessageService} from "ng-zorro-antd";
import {TabService} from "../../../../core/services/tab.service";
import {OperationInfoVO} from "../../../../core/model/operation";
import {OperationService} from "../../../../core/services/operation.service";
import {DateUtils, Objects, StringUtils} from "../../../../core/services/util.service";

@Component({
  selector: 'user-management-operation',
  templateUrl: './user-management-operation.component.html',
  styleUrls: ['./user-management-operation.component.less']
})
export class UserManagementOperationComponent implements RefreshableTab, OnInit {

  isLoading: boolean = false;
  totalPages: number = 1;
  pageIndex: number = 1;
  pageSize: number = 10;

  userName: string;
  timeRange: Date[];

  operationList: OperationInfoVO[] = [];

  needResetPageIndex: boolean = false;
  constructor(private router: Router,
              private Operation: OperationService,
              private message: NzMessageService,
              private tab: TabService) {

  }

  ngOnInit(): void {
    this.search();
  }

  search(): void {
    if (this.needResetPageIndex) {
      this.needResetPageIndex = false;
      this.pageIndex = 1;
    }
    const queryParams: TableQueryParams = {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    };
    if (!StringUtils.isEmpty(this.userName)) {
      Object.assign(queryParams, {
        userName: this.userName
      });
    }
    if (Objects.valid(this.timeRange) && this.timeRange.length === 2) {
      Object.assign(queryParams, {
        operationStartTime: DateUtils.format(this.timeRange[0]),
        operationEndTime: DateUtils.format(this.timeRange[1])
      });
    }
    this.Operation.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<OperationInfoVO>>) => {
        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        const tableResult: TableResultVO<OperationInfoVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.operationList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      });
  }

  refresh(): void {
    this.search();
  }

  resetQueryParams(): void {
    this.userName = null;
    this.timeRange = [];
    this.refresh();
  }

  resetIndex(): void {
    this.needResetPageIndex = true;
  }

}
