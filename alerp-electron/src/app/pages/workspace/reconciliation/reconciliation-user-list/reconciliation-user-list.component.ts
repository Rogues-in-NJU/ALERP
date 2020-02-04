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
import {CustomerVO} from "../../../../core/model/customer";
import {CustomerService} from "../../../../core/services/customer.service";

@Component({
  selector: 'reconciliation-user-list',
  templateUrl: './reconciliation-user-list.component.html',
  styleUrls: ['./reconciliation-user-list.component.less']
})
export class ReconciliationUserListComponent implements RefreshableTab, OnInit {

  isLoading: boolean = false;
  totalPages: number = 1;
  pageIndex: number = 1;
  pageSize: number = 20;

  customerList: CustomerVO[];

  constructor(private router: Router,
              private customer: CustomerService,
              private message: NzMessageService,
              private tab: TabService) {

  }

  ngOnInit(): void {
    this.tab.refreshEvent.subscribe((event: RefreshTabEvent) => {
      if (Objects.valid(event) && event.url === '/workspace/reconciliation/user-list') {
        this.refresh();
      }
    });
    this.refresh();
  }


  search(): void {

    const queryParams: TableQueryParams = {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize,
      type: 2, //月结
    };

    this.customer.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<CustomerVO>>) => {
        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        const tableResult: TableResultVO<CustomerVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.customerList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      });
  }

  refresh(): void {
    console.log('refresh');
    this.search();
  }

}
