import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { SummaryService } from "../../../core/services/summary.service";
import { QueryParams, ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../../../core/model/result-vm";
import { SummaryProductVO, SummaryVO } from "../../../core/model/summary";
import { HttpErrorResponse } from "@angular/common/http";
import { NzMessageService } from "ng-zorro-antd";
import { DateUtils, Objects } from "../../../core/services/util.service";

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


  timeRange: Date[];

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
    this.refresh();
  }

  onTimeRangeChange(): void {
    this.refresh();
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
          this.message.error('获取失败!');
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
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

  refresh(): void {
    this.isSummaryDataLoading = true;
    const now: Date = new Date();
    const queryParams: QueryParams = Object.assign(new QueryParams(), {
      startTime: DateUtils.of(now.getFullYear(), now.getMonth(), now.getDate()),
      endTime: DateUtils.format(now)
    });
    this.summary.getSummary(queryParams)
      .subscribe((res: ResultVO<SummaryVO>) => {
        if (!Objects.valid(res)) {
          this.message.error('获取失败!');
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
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

}
