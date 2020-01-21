import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { RefreshableTab } from "../../tab/tab.component";
import { PurchaseOrderVO } from "../../../../core/model/purchase-order";
import { PurchaseOrderService } from "../../../../core/services/purchase-order.service";
import { ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../../../../core/model/result-vm";
import { HttpErrorResponse } from "@angular/common/http";
import { NzMessageService } from "ng-zorro-antd";
import { RefreshTabEvent, TabService } from "../../../../core/services/tab.service";
import { DateUtils, Objects, StringUtils } from "../../../../core/services/util.service";

@Component({
  selector: 'purchase-order-list',
  templateUrl: './purchase-order-list.component.html',
  styleUrls: [ './purchase-order-list.component.less' ]
})
export class PurchaseOrderListComponent implements RefreshableTab, OnInit {

  isLoading: boolean = false;
  totalPages: number = 1;
  pageIndex: number = 1;
  pageSize: number = 10;

  orderCode: string;
  selectedStatus: number;
  timeRange: Date[];

  orderList: PurchaseOrderVO[] = [];

  needResetPageIndex: boolean = false;

  constructor(
    private router: Router,
    private purchaseOrder: PurchaseOrderService,
    private message: NzMessageService,
    private tab: TabService
  ) {

  }

  ngOnInit(): void {
    this.tab.refreshEvent.subscribe((event: RefreshTabEvent) => {
      if (Objects.valid(event) && event.url === '/workspace/purchase-order/list') {
        this.refresh();
      }
    });
    this.refresh();
  }

  search(): void {
    if (this.needResetPageIndex) {
      this.needResetPageIndex = false;
      this.pageIndex = 1;
    }
    const queryParams: TableQueryParams = Object.assign(new TableQueryParams(), {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    });
    if (!StringUtils.isEmpty(this.orderCode)) {
      Object.assign(queryParams, {
        id: this.orderCode
      });
    }
    if (Objects.valid(this.selectedStatus)) {
      Object.assign(queryParams, {
        status: this.selectedStatus
      });
    }
    if (Objects.valid(this.timeRange) && this.timeRange.length === 2) {
      Object.assign(queryParams, {
        doneStartTime: DateUtils.format(this.timeRange[0]),
        doneEndTime: DateUtils.format(this.timeRange[1])
      });
    }
    this.isLoading = true;
    this.purchaseOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<PurchaseOrderVO>>) => {
        if (!Objects.valid(res)) {
          this.message.error('搜索失败!');
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        const tableResult: TableResultVO<PurchaseOrderVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.orderList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
        this.isLoading = false;
      }, () => {
        this.isLoading = false;
      });
  }

  confirmAbandon(id: string): void {
    this.purchaseOrder.abandon(id)
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          this.message.error('删除失败!');
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

  resetPageIndex(): void {
    // this.pageIndex = 1;
    this.needResetPageIndex = true;
  }

  refresh(): void {
    this.search();
  }

}
