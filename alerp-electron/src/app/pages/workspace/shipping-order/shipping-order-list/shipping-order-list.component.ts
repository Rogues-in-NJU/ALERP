import {Component, OnInit} from "@angular/core";
import {RefreshableTab} from "../../tab/tab.component";
import {ShippingOrderInfoVO} from "../../../../core/model/shipping-order";
import {Router} from "@angular/router";
import {NzMessageService} from "ng-zorro-antd";
import {ShippingOrderService} from "../../../../core/services/shipping-order.service";
import {TabService} from "../../../../core/services/tab.service";
import {ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../../../../core/model/result-vm";
import {DateUtils, Objects, StringUtils} from "../../../../core/services/util.service";
import {HttpErrorResponse} from "@angular/common/http";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ProcessingOrderVO} from "../../../../core/model/processing-order";
import {ProcessingOrderService} from "../../../../core/services/processing-order.service";
import {PurchaseOrderVO} from "../../../../core/model/purchase-order";


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
  //更改查询条件时，页数重置为1
  shouldResetIndex: boolean = false;

  orderList: ShippingOrderInfoVO[] = [];

  constructor(
    private router: Router,
    private shippingOrder: ShippingOrderService,
    private processingOrder: ProcessingOrderService,
    private message: NzMessageService,
    private tab: TabService,
    private fb: FormBuilder
  ) {

  }

  ngOnInit(): void {
    this.search();
  }

  search(): void {
    this.isLoading = true;

    if(this.shouldResetIndex){
      this.pageIndex = 1;
    }
    const queryParams: TableQueryParams = {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    };

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
        status: this.selectedStatus
      });
    }
    if (Objects.valid(this.timeRange) && this.timeRange.length === 2) {
      Object.assign(queryParams, {
        createAtStartTime: DateUtils.format(this.timeRange[0]),
        createAtEndTime: DateUtils.format(this.timeRange[1])
      });
    }
    console.log(queryParams);

    this.shippingOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ShippingOrderInfoVO>>) => {
        console.log(res)
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
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
    this.isLoading = false;
  }

  confirmAbandon(id: number): void {
    console.log('confirm abandon: ' + id);
    this.shippingOrder.abandon(id)
    .subscribe((res: ResultVO<any>) => {
      console.log(res);
      if (!Objects.valid(res)) {
        return;
      }
      if (res.code !== ResultCode.SUCCESS.code) {
        return;
      }
    }, (error: HttpErrorResponse) => {
      this.message.error(error.message);
    }, () => {
    });

    // this.refresh();
  }

  refresh(): void {
    this.search();
  }

  resetQueryParams(): void{
    this.orderCode = null;
    this.customerName = null;
    this.selectedStatus = null;
    this.timeRange = [];
    this.refresh();
  }

  resetIndex(): void{
    this.shouldResetIndex = true;
  }
}
