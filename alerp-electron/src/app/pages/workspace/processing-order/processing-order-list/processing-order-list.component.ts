import { Component, OnInit } from "@angular/core";
import { NzMessageService } from "ng-zorro-antd";
import { ProcessingOrderVO } from "../../../../core/model/processing-order";
import { Router } from "@angular/router";
import { ProcessingOrderService } from "../../../../core/services/processing-order.service";
import { RefreshableTab } from "../../tab/tab.component";
import { ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../../../../core/model/result-vm";
import { HttpErrorResponse } from "@angular/common/http";
import {DateUtils, Objects} from "../../../../core/services/util.service";
import { RefreshTabEvent, TabService } from "../../../../core/services/tab.service";

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
    private message: NzMessageService,
    private tab: TabService,
  ) {

  }

  ngOnInit(): void {
    this.tab.refreshEvent.subscribe((event: RefreshTabEvent) => {
      if (Objects.valid(event) && event.url === '/workspace/processing-order/list') {
        this.refresh();
      }
    });
    this.refresh();
  }

  search(): void {
    const queryParams: TableQueryParams = Object.assign(new TableQueryParams(), {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    });
    if (Objects.valid(this.orderCode)) {
      queryParams['id'] = this.orderCode;
      this.orderCode = null;
    }
    if (Objects.valid(this.selectedStatus)) {
      queryParams['status'] = this.selectedStatus;
      this.selectedStatus = null;
    }
    if (Objects.valid(this.timeRange)) {
      queryParams['createAtStartTime'] = DateUtils.format(this.timeRange[0]);
      queryParams['createAtEndTime'] = DateUtils.format(this.timeRange[1]);
      this.timeRange = null;
    }

    console.log(queryParams);

    this.isLoading = true;
    this.processingOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ProcessingOrderVO>>) => {
        console.log(res);

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
