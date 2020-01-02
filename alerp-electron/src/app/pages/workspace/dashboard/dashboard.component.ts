import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { SummaryService } from "../../../core/services/summary.service";
import { ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../../../core/model/result-vm";
import { SummaryProductVO, SummaryVO } from "../../../core/model/summary";
import { HttpErrorResponse } from "@angular/common/http";
import { NzMessageService } from "ng-zorro-antd";
import { Objects } from "../../../core/services/util.service";

@Component({
  selector: 'workspace-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: [ './dashboard.component.less' ]
})
export class DashboardComponent implements OnInit {

  isSummaryDataLoading: boolean = false;
  isSummaryProductsLoading: boolean = false;

  summaryData: SummaryVO;
  summaryProducts: SummaryProductVO[];

  pageIndex: number = 1;
  pageSize: number = 10;
  totalPages: number = 1;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private summary: SummaryService,
    private message: NzMessageService
  ) {

  }

  ngOnInit(): void {
    this.isSummaryDataLoading = true;
    this.summary.getSummary()
      .subscribe((res: ResultVO<SummaryVO>) => {
      if (!Objects.valid(res)) {
        return;
      }
      if (res.code !== ResultCode.SUCCESS.code) {
        return;
      }
      this.summaryData = res.data;
      this.isSummaryDataLoading = false;
    }, (error: HttpErrorResponse) => {
      this.message.error(error.message);
    }, () => {

    });
    this.search();
  }

  search(): void {
    this.isSummaryProductsLoading = true;
    const queryParams: TableQueryParams = Object.assign(new TableQueryParams(), {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    });
    this.summary.getSummaryProducts(queryParams)
      .subscribe((res: ResultVO<TableResultVO<SummaryProductVO>>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        const tableResults: TableResultVO<SummaryProductVO> = res.data;
        this.summaryProducts = tableResults.result;
        this.pageIndex = tableResults.pageIndex;
        this.pageSize = tableResults.pageSize;
        this.totalPages = tableResults.totalPages;
        this.isSummaryProductsLoading = false;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      }, () => {

      });
  }

}
