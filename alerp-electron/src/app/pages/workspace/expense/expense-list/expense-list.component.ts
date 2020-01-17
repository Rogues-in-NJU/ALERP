import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {RefreshableTab} from "../../tab/tab.component";
import {ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../../../../core/model/result-vm";
import {HttpErrorResponse} from "@angular/common/http";
import {NzMessageService} from "ng-zorro-antd";
import {TabService, RefreshTabEvent} from "../../../../core/services/tab.service";
import {ExpenseService} from "../../../../core/services/expense.service";
import {ExpenseInfoVO} from "../../../../core/model/expense";
import {Objects} from "../../../../core/services/util.service";

@Component({
  selector: 'expense-list',
  templateUrl: './expense-list.component.html',
  styleUrls: ['./expense-list.component.less']
})
export class ExpenseListComponent implements RefreshableTab, OnInit {

  isLoading: boolean = false;
  totalPages: number = 1;
  pageIndex: number = 1;
  pageSize: number = 10;

  expenseList: ExpenseInfoVO[] = [];

  constructor(private router: Router,
              private Expense: ExpenseService,
              private message: NzMessageService,
              private tab: TabService) {

  }

  ngOnInit(): void {
    this.tab.refreshEvent.subscribe((event: RefreshTabEvent) => {
      if (Objects.valid(event) && event.url === '/workspace/expense/list') {
        this.refresh();
      }
    });
    this.refresh();
  }

  search(): void {
    const queryParams: TableQueryParams = {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    };
    this.Expense.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ExpenseInfoVO>>) => {
        if (!res) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        const tableResult: TableResultVO<ExpenseInfoVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.expenseList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });

  }

  confirmAbandon(id: string): void {
    this.Expense.abandon(id)
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.message.success('删除成功!');
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
        this.refresh();
      }, () => {
        this.refresh();
      });
  }

  refresh(): void {
    this.search();
  }

}
