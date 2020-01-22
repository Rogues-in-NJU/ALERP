import { Component, OnInit } from "@angular/core";
import { RefreshableTab } from "../../tab/tab.component";
import { ArrearOrderInfoVO } from "../../../../core/model/arrear-order";
import { Router } from "@angular/router";
import { NzMessageService } from "ng-zorro-antd";
import { TabService } from "../../../../core/services/tab.service";
import { ArrearOrderService } from "../../../../core/services/arrear-order.service";
import {ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../../../../core/model/result-vm";
import {DateUtils, Objects, StringUtils} from "../../../../core/services/util.service";
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
//更改查询条件时，页数重置为1
  shouldResetIndex: boolean = false;

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
    this.isLoading = true;

    if(this.shouldResetIndex){
      this.pageIndex = 1;
      this.shouldResetIndex = false;
    }
    const queryParams: TableQueryParams = Object.assign(new TableQueryParams(), {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    });

    if (!StringUtils.isEmpty(this.query_customerName)) {
      Object.assign(queryParams, {
        customerName: this.query_customerName
      });
    }
    if (!StringUtils.isEmpty(this.query_invoiceNumber)) {
      Object.assign(queryParams, {
        invoiceNumber: this.query_invoiceNumber
      });
    }
    if (Objects.valid(this.query_selectedStatus)) {
      Object.assign(queryParams, {
        status: this.query_selectedStatus
      });
    }
    if (Objects.valid(this.query_timeRange) && this.query_timeRange.length === 2) {
      Object.assign(queryParams, {
        createAtStartTime: DateUtils.format(this.query_timeRange[0]),
        createAtEndTime: DateUtils.format(this.query_timeRange[1])
      });
    }
    console.log(queryParams);

    this.arrearOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ArrearOrderInfoVO>>) => {
        console.log(res)
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        const tableResult: TableResultVO<ArrearOrderInfoVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.orderList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      }, () => {
        this.isLoading = false;
      });
  }

  resetQueryParams(): void{
    this.query_customerName = null;
    this.query_invoiceNumber = null;
    this.query_selectedStatus = null;
    this.query_timeRange = [];
    this.refresh();
  }

  resetIndex(): void{
    this.shouldResetIndex = true;
  }
}
