import { Component, OnInit } from "@angular/core";
import { CloseTabEvent, TabService } from "../../../../core/services/tab.service";
import { ActivatedRoute, Router } from "@angular/router";
import { PurchaseOrderVO } from "../../../../core/model/purchase-order";
import { ClosableTab } from "../../tab/tab.component";
import { PurchaseOrderService } from "../../../../core/services/purchase-order.service";
import { ResultVO } from "../../../../core/model/result-vm";
import { HttpErrorResponse } from "@angular/common/http";
import { NzMessageService } from "ng-zorro-antd";
import { Objects } from "../../../../core/services/util.service";

@Component({
  selector: 'purchase-order-info',
  templateUrl: './purchase-order-info.component.html',
  styleUrls: [ './purchase-order-info.component.less' ]
})
export class PurchaseOrderInfoComponent implements ClosableTab, OnInit {

  isLoading: boolean = true;
  purchaseOrderId: number;
  purchaseOrderData: PurchaseOrderVO;

  constructor(
    private tab: TabService,
    private route: ActivatedRoute,
    private router: Router,
    private purchaseOrder: PurchaseOrderService,
    private message: NzMessageService
  ) {

  }

  ngOnInit(): void {
    this.purchaseOrderId = this.route.snapshot.params[ 'id' ];
    this.purchaseOrder.find(this.purchaseOrderId)
      .subscribe((res: ResultVO<PurchaseOrderVO>) => {
        if (!Objects.valid(res)) {
          return;
        }
        this.isLoading = false;
        this.purchaseOrderData = res.data;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  tabClose(): void {
    // this.tab.closeEvent.emit({
    //   url: this.router.url,
    //   refreshUrl: null,
    //   routeConfig: this.route.snapshot.routeConfig
    // });
  }

}
