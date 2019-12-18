import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {RefreshableTab} from "../../tab/tab.component";
import {AuthService} from "../../../../core/services/user.service";
import {ResultVO, TableQueryParams, TableResultVO} from "../../../../core/model/result-vm";
import {HttpErrorResponse} from "@angular/common/http";
import {NzMessageService} from "ng-zorro-antd";
import {TabService} from "../../../../core/services/tab.service";
import {ExpenseService} from "../../../../core/services/expense.service";
import {ExpenseInfoVO} from "../../../../core/model/expense";

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
              private auth: AuthService,
              private Expense: ExpenseService,
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
    this.Expense.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ExpenseInfoVO>>) => {
        if (!res) {
          return;
        }
        if (res.code !== 200) {
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
    console.log('confirm abandon: ' + id);
  }

  refresh(): void {
  }

}
