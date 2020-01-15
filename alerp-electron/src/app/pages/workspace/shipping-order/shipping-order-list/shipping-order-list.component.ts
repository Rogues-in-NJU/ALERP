import {Component, OnInit, ViewChild, ElementRef} from "@angular/core";
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
import {ENgxPrintComponent} from "e-ngx-print";


@Component({
  selector: 'shipping-order-list',
  templateUrl: './shipping-order-list.component.html',
  styleUrls: ['./shipping-order-list.component.less']
})
export class ShippingOrderListComponent implements RefreshableTab, OnInit {

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

  totalMoney: number;
  printCSS: string[];
  printStyle: string;

  constructor(private router: Router,
              private shippingOrder: ShippingOrderService,
              private processingOrder: ProcessingOrderService,
              private message: NzMessageService,
              private tab: TabService,
              private fb: FormBuilder,
              private elRef: ElementRef) {
    this.printCSS = ['http://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css'];

    this.printStyle =
      `
        th, td {
            color: blue !important;
        }
        `;
  }

  ngOnInit(): void {
    this.search();
  }

  search(): void {
    this.isLoading = true;

    if (this.shouldResetIndex) {
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
    // console.log(queryParams);

    this.shippingOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ShippingOrderInfoVO>>) => {
        // console.log(res)
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
        // console.log(this.orderList);
        this.totalMoney = 0;
        for (const o of this.orderList) {
          if (o.status !== 2) {
            this.totalMoney = this.totalMoney + o.receivableCash;
          }
          // console.log(this.totalMoney);
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
    this.isLoading = false;
  }

  confirmAbandon(id: number): void {
    // console.log('confirm abandon: ' + id);
    this.shippingOrder.abandon(id)
      .subscribe((res: ResultVO<any>) => {
        // console.log(res);
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

  resetQueryParams(): void {
    this.orderCode = null;
    this.customerName = null;
    this.selectedStatus = null;
    this.timeRange = [];
    this.refresh();
  }

  resetIndex(): void {
    this.shouldResetIndex = true;
  }


  @ViewChild('print1', {static: false})
  printComponent: ENgxPrintComponent;
  showPrint: boolean = false;

  printComplete() {
    // console.log('打印完成！');
    this.showPrint = false;
  }

  customPrint(print: string) {
    this.showPrint = true;
    const printHTML: any = this.elRef.nativeElement.childNodes[2];
    this.printComponent.print(printHTML);
  }
}
