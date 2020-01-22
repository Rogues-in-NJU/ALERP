import { Component, OnInit } from "@angular/core";
import { RefreshableTab } from "../../tab/tab.component";
import {
  AllOverDuesVO,
  ArrearOrderInfoVO,
  ArrearStatisticsVO,
  CustomerOverduesVO
} from "../../../../core/model/arrear-order";
import { Router } from "@angular/router";
import { NzMessageService } from "ng-zorro-antd";
import { TabService } from "../../../../core/services/tab.service";
import { ArrearOrderService } from "../../../../core/services/arrear-order.service";
import {ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../../../../core/model/result-vm";
import { Objects } from "../../../../core/services/util.service";
import { HttpErrorResponse } from "@angular/common/http";
import {CustomerVO} from "../../../../core/model/customer";

@Component({
  selector: 'arrear-order-statistics',
  templateUrl: './arrear-order-statistics.component.html',
  styleUrls: [ './arrear-order-statistics.component.less' ]
})
export class ArrearOrderStatisticsComponent implements RefreshableTab, OnInit {

  arrearStatisticsData : ArrearStatisticsVO = {};
  customers: CustomerOverduesVO[];
  statistics: AllOverDuesVO = {};
  isLoading : boolean = false;

  constructor(
    private arrearOrder: ArrearOrderService,
    private message: NzMessageService,
  ) {
  }

  ngOnInit(): void {
    this.isLoading = true;
    this.arrearOrder.getArrearStatistics()
      .subscribe((res: ResultVO<ArrearStatisticsVO>) => {
        console.log(res);
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        this.arrearStatisticsData = res.data;
        this.customers = this.arrearStatisticsData.customers;
        this.statistics = this.arrearStatisticsData.statistics;
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
        this.isLoading = false;
      }, () => {
        this.isLoading = false;
      });
  }

  refresh(): void {
  }
}
