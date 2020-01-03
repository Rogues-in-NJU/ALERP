import { Component, OnInit } from "@angular/core";
import { RefreshableTab } from "../../tab/tab.component";
import { ArrearOrderInfoVO } from "../../../../core/model/arrear-order";
import { Router } from "@angular/router";
import { NzMessageService } from "ng-zorro-antd";
import { TabService } from "../../../../core/services/tab.service";
import { ArrearOrderService } from "../../../../core/services/arrear-order.service";
import { ResultVO, TableQueryParams, TableResultVO } from "../../../../core/model/result-vm";
import { Objects } from "../../../../core/services/util.service";
import { HttpErrorResponse } from "@angular/common/http";

@Component({
  selector: 'arrear-order-list',
  templateUrl: './arrear-order-list.component.html',
  styleUrls: [ './arrear-order-list.component.less' ]
})
export class ArrearOrderListComponent implements RefreshableTab, OnInit {

  isLoading: boolean = false;
  totalPages: number = 1;
  pageIndex: number = 1;
  pageSize: number = 10;

  query_customerName: string;
  query_invoiceNumber: string;
  query_selectedStatus: number;
  query_timeRange: Date[];

  orderList: ArrearOrderInfoVO[];

  constructor(
    private router: Router,
    private arrearOrder: ArrearOrderService,
    private message: NzMessageService,
    private tab: TabService
  ) {

  }

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.search();
  }

  search(): void{
    const queryParams: TableQueryParams = Object.assign(new TableQueryParams(), {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    });
    this.isLoading = true;
    this.arrearOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ArrearOrderInfoVO>>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== 200) {
          return;
        }
        const tableResult: TableResultVO<ArrearOrderInfoVO> = res.data;
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
}
