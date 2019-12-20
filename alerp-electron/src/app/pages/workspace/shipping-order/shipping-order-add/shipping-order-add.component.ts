import {Component, OnInit} from "@angular/core";
import {RefreshableTab} from "../../tab/tab.component";

@Component({
  selector: 'shipping-order-add',
  templateUrl: './shipping-order-add.component.html',
  styleUrls: [ './shipping-order-add.component.less' ]
})
export class ShippingOrderAddComponent implements RefreshableTab, OnInit{
  ngOnInit(): void {
  }

  refresh(): void {
  }

}
