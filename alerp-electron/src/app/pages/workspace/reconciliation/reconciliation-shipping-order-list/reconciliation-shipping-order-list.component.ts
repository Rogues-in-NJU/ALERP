import {Component, OnInit, ViewChild, ElementRef} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {RefreshableTab} from "../../tab/tab.component";
import {UserManagementService} from "../../../../core/services/user-management.service";
import {QueryParams, ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../../../../core/model/result-vm";
import {HttpErrorResponse} from "@angular/common/http";
import {NzMessageService} from "ng-zorro-antd";
import {TabService, RefreshTabEvent} from "../../../../core/services/tab.service";
import {UserManagementInfoVO} from "../../../../core/model/user-management";
import {StringUtils, Objects} from "../../../../core/services/util.service";
import {ReconciliationService} from "../../../../core/services/reconciliation.service";
import {ShippingOrderInfoVO} from "../../../../core/model/shipping-order";
import {ENgxPrintComponent} from "e-ngx-print";

@Component({
  selector: 'reconciliation-shipping-order-list',
  templateUrl: './reconciliation-shipping-order-list.component.html',
  styleUrls: ['./reconciliation-shipping-order-list.component.less']
})
export class ReconciliationShippingOrderListComponent implements RefreshableTab, OnInit {

  isLoading: boolean = true;
  totalPages: number = 1;
  pageIndex: number = 1;
  pageSize: number = 200;

  shippingOrderList: ShippingOrderInfoVO[];
  toPrintList: ShippingOrderInfoVO[] = [];
  toPrintMoney: number;
  customerId: number;

  isPrintButtonAvailiable: boolean = false;


  isInvoiceButtonAvailiable: boolean = false;
  isCheckedOrderReconciliationed: boolean = false;
  isCheckedOrderNotReconciliationed: boolean = false;

  isAllDisplayDataChecked: boolean;
  isIndeterminate: boolean;
  mapOfCheckedId: {[key: number]: boolean} = {};
  numberOfChecked = 0;

  isInvoiceModalVisible: boolean = false;
  invoiceNumber: string = "";

  printCSS: string[];

  printStyle: string;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private UserManagement: UserManagementService,
              private message: NzMessageService,
              private reconciliation: ReconciliationService,
              private tab: TabService,
              private elRef: ElementRef) {
    this.customerId = this.route.snapshot.params['id'];

    this.printCSS = ['http://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css'];

    this.printStyle =
      `
        th, td {
            color: blue !important;
        }
        `;
  }

  ngOnInit(): void {
    this.tab.refreshEvent.subscribe((event: RefreshTabEvent) => {
      if (Objects.valid(event) && event.url === '/workspace/reconciliation/user-list') {
        this.refresh();
      }
    });
    this.refresh();
  }


  search(): void {

    const queryParams: TableQueryParams = {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize,
      customerId: this.customerId,
    };

    this.reconciliation.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ShippingOrderInfoVO>>) => {
        console.log(res);
        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        const tableResult: TableResultVO<ShippingOrderInfoVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.shippingOrderList = tableResult.result;
        this.isLoading = false;
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      });
  }


  refresh(): void {
    console.log('refresh');
    this.search();
  }


  changeReconciliation(shippingOrderId: number): void {
    const queryParams: QueryParams = {
      shippingOrderIds: [shippingOrderId],
      toState: 0,
    };
    this.reconciliation.reconciliationAll(queryParams)
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }

      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      }, () => {
        this.refresh();
      });
  }

  beginAddInvoiceNumber(): void {
    let isAllReconciliationed: boolean = true;

    let checkedIds: number[] = [];
    for (let shippingOrder of this.shippingOrderList) {
      if (this.mapOfCheckedId[shippingOrder.id]) {
        checkedIds.push(shippingOrder.id);
        if (shippingOrder.hasReconciliationed == 0) {
          isAllReconciliationed = false;
        }
      }
    }
    //
    //
    // isAllReconciliationed = this.shippingOrderList
    //   .filter(item => this.mapOfCheckedId[item.id])
    //   .every(item => item.hasReconciliationed === 1);

    if (!isAllReconciliationed || checkedIds.length == 0) {
      this.message.warning("请选择已对账过的出货单进行操作！");
      return;
    }

    this.isInvoiceModalVisible = true;

  }

  confirmAddInvoiceNumber(): void {
    let checkedIds: number[] = [];
    for (let shippingOrder of this.shippingOrderList) {
      if (this.mapOfCheckedId[shippingOrder.id]) {
        checkedIds.push(shippingOrder.id);
      }
    }

    const queryParams: QueryParams = {
      invoiceNumber: this.invoiceNumber,
      shippingOrderIds: checkedIds,
    };
    console.log(queryParams);
    this.reconciliation.changeAllInvoiceNumber(queryParams)
      .subscribe((res: ResultVO<any>) => {
        console.log(res);
        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }

      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      }, () => {
        this.isInvoiceModalVisible = false;
        this.invoiceNumber = "";
        this.refresh();
      });
  }

  cancelAddInvoiceNumber(): void {
    this.invoiceNumber = "";
    this.isInvoiceModalVisible = false;
  }

  @ViewChild('print1', {static: false})
  printComponent: ENgxPrintComponent;
  showPrint: boolean = false;
  isPreview: boolean = false;

  printComplete() {
    this.showPrint = false;
  }

  handleCancel(): void {
    this.isPreview = false;
  }

  preview(): void {
    this.toPrintMoney = 0;
    for (let shippingOrder of this.shippingOrderList) {
      if (this.mapOfCheckedId[shippingOrder.id]) {
        this.toPrintList.push(shippingOrder);
        this.toPrintMoney += shippingOrder.receivableCash;
      }
    }
    this.isPreview = true;
  }

  customPrint(): void {
    let isAllUnReconciliationed: boolean = true;
    let checkedIds: number[] = [];
    for (let shippingOrder of this.shippingOrderList) {
      if (this.mapOfCheckedId[shippingOrder.id]) {
        checkedIds.push(shippingOrder.id);
        if (shippingOrder.hasReconciliationed == 1) {
          isAllUnReconciliationed = false;
        }
      }
    }

    if (!isAllUnReconciliationed || checkedIds.length == 0) {
      this.message.warning("请选择未对账过的出货单进行操作！");
      return;
    }
    this.showPrint = true;
    const printHTML: any = this.elRef.nativeElement.childNodes[2];
    this.printComponent.print(printHTML);

    this.toReconciliation(checkedIds);

  }

  toReconciliation(checkedIds: number[]): void {

    const queryParams: QueryParams = {
      toState: 1,
      shippingOrderIds: checkedIds,
    };

    console.log(queryParams);
    this.reconciliation.reconciliationAll(queryParams)
      .subscribe((res: ResultVO<any>) => {
        console.log(res);
        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }

      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      }, () => {
        this.refresh();
      });
  }
}
