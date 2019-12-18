import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { RefreshableTab } from "../../tab/tab.component";
import { PurchaseOrderInfoVO } from "../../../../core/model/purchase-order";
import { AuthService } from "../../../../core/services/user.service";
import { PurchaseOrderService } from "../../../../core/services/purchase-order.service";
import { ResultVO, TableQueryParams, TableResultVO } from "../../../../core/model/result-vm";
import { HttpErrorResponse } from "@angular/common/http";
import { NzMessageService } from "ng-zorro-antd";
import { TabService } from "../../../../core/services/tab.service";

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

  orderId: string;
  selectedStatus: number;
  timeRange: Date[];

  orderList: PurchaseOrderInfoVO[] = [];

  constructor(
    private router: Router,
    private auth: AuthService,
    private purchaseOrder: PurchaseOrderService,
    private message: NzMessageService,
    private tab: TabService
  ) {

  }

  ngOnInit(): void {
    this.search();
  }

  search(): void {
    // console.log(this.orderId);
    // console.log(this.selectedStatus);
    // console.log(this.timeRange);
    const queryParams: TableQueryParams = {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    };
    this.purchaseOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<PurchaseOrderInfoVO>>) => {
        if (!res) {
          return;
        }
        if (res.code !== 200) {
          return;
        }
        const tableResult: TableResultVO<PurchaseOrderInfoVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.orderList = tableResult.result;
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
