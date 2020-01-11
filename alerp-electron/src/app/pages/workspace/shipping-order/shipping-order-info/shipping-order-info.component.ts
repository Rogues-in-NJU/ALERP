import {Component, OnInit} from "@angular/core";
import {ClosableTab, RefreshableTab} from "../../tab/tab.component";
import {ShippingOrderInfoVO, ShippingOrderProductInfoVO} from "../../../../core/model/shipping-order";
import {TabService} from "../../../../core/services/tab.service";
import {ActivatedRoute, Router} from "@angular/router";
import {NzMessageService} from "ng-zorro-antd";
import {ShippingOrderService} from "../../../../core/services/shipping-order.service";
import {ProductVO} from "../../../../core/model/product";
import {BehaviorSubject, Observable} from "rxjs";
import {QueryParams, ResultCode, ResultVO} from "../../../../core/model/result-vm";
import {debounceTime, map, switchMap} from "rxjs/operators";
import {ProcessingOrderProductVO, ProcessingOrderVO} from "../../../../core/model/processing-order";
import {Objects, SpecificationUtils, StringUtils} from "../../../../core/services/util.service";
import {HttpErrorResponse} from "@angular/common/http";
import {ProductService} from "../../../../core/services/product.service";

@Component({
  selector: 'shipping-order-info',
  templateUrl: './shipping-order-info.component.html',
  styleUrls: ['./shipping-order-info.component.less']
})
export class ShippingOrderInfoComponent implements ClosableTab, OnInit {

  isLoading: boolean = true;
  shippingOrderCode: string;
  shippingOrderId: number;
  shippingOrderData: ShippingOrderInfoVO = {};

  processingOrderInfoProductCountIndex: number = 0;

  printCSS: string[];
  printStyle: string;

  constructor(private closeTabService: TabService,
              private route: ActivatedRoute,
              private router: Router,
              private shippingOrder: ShippingOrderService,
              private product: ProductService,
              private message: NzMessageService) {
    this.shippingOrderCode = this.route.snapshot.params['code'];

    this.printCSS = ['http://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css'];

    this.printStyle =
      `
        th, td {
            color: blue !important;
        }
        `;
  }


  printComplete() {
    console.log('打印完成！');
  }

  ngOnInit(): void {
    this.shippingOrderId = this.route.snapshot.params['id'];
    this.reload();
  }

  tabClose(): void {
    this.closeTabService.closeEvent.emit({
      url: this.router.url,
      refreshUrl: null,
      routeConfig: this.route.snapshot.routeConfig
    });
  }

  reload(): void {
    this.shippingOrder.find(this.shippingOrderId)
      .subscribe((res: ResultVO<ShippingOrderInfoVO>) => {
        console.log(res);
        if (!Objects.valid(res)) {
          return;
        }
        this.isLoading = false;
        this.shippingOrderData = res.data;
        if (Objects.valid(this.shippingOrderData.products)) {
          this.shippingOrderData.products.forEach(item => {
            item['_id'] = this.processingOrderInfoProductCountIndex++;
          })
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

}
