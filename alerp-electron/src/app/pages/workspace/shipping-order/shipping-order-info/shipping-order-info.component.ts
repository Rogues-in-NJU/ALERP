import {Component, OnInit} from "@angular/core";
import {ClosableTab, RefreshableTab} from "../../tab/tab.component";
import {ShippingOrderInfoVO} from "../../../../core/model/shipping-order";
import {TabService} from "../../../../core/services/tab.service";
import {ActivatedRoute, Router} from "@angular/router";
import {PurchaseOrderService} from "../../../../core/services/purchase-order.service";
import {NzMessageService} from "ng-zorro-antd";
import {ShippingOrderService} from "../../../../core/services/shipping-order.service";

@Component({
  selector: 'shipping-order-info',
  templateUrl: './shipping-order-info.component.html',
  styleUrls: [ './shipping-order-info.component.less' ]
})
export class ShippingOrderInfoComponent implements ClosableTab, OnInit{

  isLoading: boolean = true;
  shippingOrderCode: string;
  shippingOrderData: ShippingOrderInfoVO;

  constructor(
    private closeTabService: TabService,
    private route: ActivatedRoute,
    private router: Router,
    private shippingOrder: ShippingOrderService,
    private message: NzMessageService
  ) {
    this.shippingOrderCode = this.route.snapshot.params['code'];
  }

  ngOnInit(): void {
  }

  tabClose(): void {
    this.closeTabService.closeEvent.emit({
      url: this.router.url,
      refreshUrl: null,
      routeConfig: this.route.snapshot.routeConfig
    });
  }

}
