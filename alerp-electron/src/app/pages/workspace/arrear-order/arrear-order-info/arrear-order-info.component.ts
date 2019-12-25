import {Component, OnInit} from "@angular/core";
import {RefreshableTab} from "../../tab/tab.component";

@Component({
  selector: 'arrear-order-info',
  templateUrl: './arrear-order-info.component.html',
  styleUrls: [ './arrear-order-info.component.less' ]
})
export class ArrearOrderInfoComponent implements RefreshableTab, OnInit {
  ngOnInit(): void {
  }

  refresh(): void {
  }
}
