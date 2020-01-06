import { Component, OnInit } from "@angular/core";
import { NzMessageService } from "ng-zorro-antd";
import { ProcessingOrderVO } from "../../../../core/model/processing-order";
import { Router } from "@angular/router";
import { ProcessingOrderService } from "../../../../core/services/processing-order.service";
import { RefreshableTab } from "../../tab/tab.component";
import { ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../../../../core/model/result-vm";
import { HttpErrorResponse } from "@angular/common/http";
import { DateUtils, Objects, StringUtils } from "../../../../core/services/util.service";
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

  orderCode: string;
  customerName: string;
  selectedStatus: number;
  timeRange: Date[];

  orderList: ProcessingOrderVO[] = [];

  constructor(
    private router: Router,
    private processingOrder: ProcessingOrderService,
    private message: NzMessageService,
    private tab: TabService
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
    console.log(this.orderCode);
    console.log(this.selectedStatus);
    console.log(this.timeRange);

    const queryParams: TableQueryParams = Object.assign(new TableQueryParams(), {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    });
    if (!StringUtils.isEmpty(this.orderCode)) {
      Object.assign(queryParams, {
        id: this.orderCode
      });
    }
    if (!StringUtils.isEmpty(this.customerName)) {
      Object.assign(queryParams, {
        customerName: this.customerName
      });
    }
    if (Objects.valid(this.selectedStatus)) {
      Object.assign(queryParams, {
        statue: this.selectedStatus
      });
    }
    if (Objects.valid(this.timeRange) && this.timeRange.length === 2) {
      Object.assign(queryParams, {
        createAtStartTime: DateUtils.format(this.timeRange[0]),
        createAtEndTime: DateUtils.format(this.timeRange[1])
      });
    }
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
