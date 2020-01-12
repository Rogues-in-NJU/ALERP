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

  constructor(private router: Router,
              private Operation: OperationService,
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
    if (!StringUtils.isEmpty(this.userName)) {
      Object.assign(queryParams, {
        userName: this.userName
      });
      this.userName = null;
    }
    if (Objects.valid(this.timeRange) && this.timeRange.length === 2) {
      Object.assign(queryParams, {
        operationStartTime: DateUtils.format(this.timeRange[0]),
        operationEndTime: DateUtils.format(this.timeRange[1])
      });
    }

    this.Operation.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<OperationInfoVO>>) => {
        if (!res) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        const tableResult: TableResultVO<OperationInfoVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.operationList = tableResult.result;
        console.log(this.operationList);
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  refresh(): void {
  }

}
