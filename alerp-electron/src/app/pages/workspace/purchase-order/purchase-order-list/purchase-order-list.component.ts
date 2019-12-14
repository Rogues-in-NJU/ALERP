import { Component } from "@angular/core";
import { CloseTabService } from "../../../../core/services/event/close-tab.service";
import { Router } from "@angular/router";
import { ClosableTab } from "../../tab/tab.component";

@Component({
  selector: 'purchase-order-list',
  templateUrl: './purchase-order-list.component.html',
  styleUrls: [ './purchase-order-list.component.less' ]
})
export class PurchaseOrderListComponent implements ClosableTab {

  orderId: string;
  selectedStatus: number;
  timeRange: Date[];

  listOfData = [
    {
      key: '1',
      name: 'John Brown',
      age: 32,
      address: 'New York No. 1 Lake Park'
    },
    {
      key: '2',
      name: 'Jim Green',
      age: 42,
      address: 'London No. 1 Lake Park'
    },
    {
      key: '3',
      name: 'Joe Black',
      age: 32,
      address: 'Sidney No. 1 Lake Park'
    }
  ];

  orderList: PurchaseOrderListVO[] = [{
    id: '0001000',
    description: '',
    cash: 20,
    salesman: '',
    purchaseTime: '2019-10-10 12:00',
    status: '已完成'
  }];

  constructor(
    private closeTabService: CloseTabService,
    private router: Router
  ) {

  }

  closeTab(): void {
    console.log(this.router.url);
    this.closeTabService.event.emit({
      url: this.router.url,
      goToUrl: ''
    });
  }

  search(): void {
    console.log(this.orderId);
    console.log(this.selectedStatus);
    console.log(this.timeRange);
  }

}

export interface PurchaseOrderListVO {

  id: string;
  description: string;
  cash: number;
  salesman: string;
  purchaseTime: string;
  status: string

}
