import {Component, OnInit} from "@angular/core";
import {RefreshableTab} from "../../tab/tab.component";
import {ShippingOrderInfoVO} from "../../../../core/model/shipping-order";
import {Router} from "@angular/router";
import {AuthService} from "../../../../core/services/user.service";
import {NzMessageService} from "ng-zorro-antd";
import {ShippingOrderService} from "../../../../core/services/shipping-order.service";
import {TabService} from "../../../../core/services/tab.service";
import {ResultVO, TableQueryParams, TableResultVO} from "../../../../core/model/result-vm";
import {Objects} from "../../../../core/services/util.service";
import {HttpErrorResponse} from "@angular/common/http";
import { PurchaseOrderVO } from "../../../../core/model/purchase-order";

@Component({
  selector: 'shipping-order-list',
  templateUrl: './shipping-order-list.component.html',
  styleUrls: [ './shipping-order-list.component.less' ]
})
export class ShippingOrderListComponent implements RefreshableTab, OnInit{

  isLoading: boolean = false;
  totalPages: number = 1;
  pageIndex: number = 1;
  pageSize: number = 10;

  orderId: string;
  //queryParams:
  orderCode: string;
  customerName: string;
  selectedStatus: number;
  timeRange: Date[];

  orderList: ShippingOrderInfoVO[] = [];

  constructor(
    private router: Router,
    private auth: AuthService,
    private shippingOrder: ShippingOrderService,
    private message: NzMessageService,
    private tab: TabService
  ) {

  }

  ngOnInit(): void {
    this.search();
  }

  search(): void {
    const queryParams: TableQueryParams = {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    };
    this.shippingOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ShippingOrderInfoVO>>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== 200) {
          return;
        }
        const tableResult: TableResultVO<ShippingOrderInfoVO> = res.data;

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
