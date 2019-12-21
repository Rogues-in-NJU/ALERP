import { Component, OnInit } from "@angular/core";
import { NzMessageService } from "ng-zorro-antd";
import { ProcessingOrderVO } from "../../../../core/model/processing-order";
import { Router } from "@angular/router";
import { ProcessingOrderService } from "../../../../core/services/processing-order.service";
import { RefreshableTab } from "../../tab/tab.component";
import { ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../../../../core/model/result-vm";
import { HttpErrorResponse } from "@angular/common/http";
import { Objects } from "../../../../core/services/util.service";

@Component({
  selector: 'processing-order-list',
  templateUrl: './processing-order-list.component.html',
  styleUrls: [ './processing-order-list.component.less' ]
})
export class ProcessingOrderListComponent implements RefreshableTab, OnInit {

  isLoading: boolean = false;
  totalPages: number = 1;
  pageIndex: number = 1;
  pageSize: number = 10;

  orderCode: number;
  selectedStatus: number;
  timeRange: Date[];

  orderList: ProcessingOrderVO[] = [];

  constructor(
    private router: Router,
    private processingOrder: ProcessingOrderService,
    private message: NzMessageService
  ) {

  }

  ngOnInit(): void {
    this.refresh();
  }

  search(): void {
    console.log(this.orderCode);
    console.log(this.selectedStatus);
    console.log(this.timeRange);

    const queryParams: TableQueryParams = Object.assign(new TableQueryParams(), {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    });
    this.isLoading = true;
    this.processingOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ProcessingOrderVO>>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        const tableResult: TableResultVO<ProcessingOrderVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.orderList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      }, () => {
        this.isLoading = false;
      });
  }

  confirmAbandon(id: number): void {
    console.log('confirm abandon: ' + id);
  }

  refresh(): void {
    this.search();
  }

}
