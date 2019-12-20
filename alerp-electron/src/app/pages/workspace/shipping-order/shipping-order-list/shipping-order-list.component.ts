import {Component, OnInit} from "@angular/core";
import {RefreshableTab} from "../../tab/tab.component";

@Component({
  selector: 'shipping-order-list',
  templateUrl: './shipping-order-list.component.html',
  styleUrls: [ './shipping-order-list.component.less' ]
})
export class ShippingOrderListComponent implements RefreshableTab, OnInit{
  ngOnInit(): void {
  }

  refresh(): void {
  }

}
