import {Component, OnInit} from "@angular/core";
import {RefreshableTab} from "../../tab/tab.component";

@Component({
  selector: 'shipping-order-info',
  templateUrl: './shipping-order-info.component.html',
  styleUrls: [ './shipping-order-info.component.less' ]
})
export class ShippingOrderInfoComponent implements RefreshableTab, OnInit{
  ngOnInit(): void {
  }

  refresh(): void {
  }

}
