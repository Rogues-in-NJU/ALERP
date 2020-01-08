import {Component, OnInit} from "@angular/core";
import {RefreshableTab} from "../../tab/tab.component";
import {ShippingOrderInfoVO} from "../../../../core/model/shipping-order";
import {Router} from "@angular/router";
import {NzMessageService} from "ng-zorro-antd";
import {ShippingOrderService} from "../../../../core/services/shipping-order.service";
import {TabService} from "../../../../core/services/tab.service";
import {ResultCode, ResultVO, TableQueryParams, TableResultVO} from "../../../../core/model/result-vm";
import {Objects} from "../../../../core/services/util.service";
import {HttpErrorResponse} from "@angular/common/http";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ProcessingOrderVO} from "../../../../core/model/processing-order";
import {ProcessingOrderService} from "../../../../core/services/processing-order.service";


@Component({
  selector: 'shipping-order-list',
  templateUrl: './shipping-order-list.component.html',
  styleUrls: [ './shipping-order-list.component.less' ]
})
export class ShippingOrderListComponent implements RefreshableTab, OnInit{

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

  orderList: ShippingOrderInfoVO[] = [];

  //modal
  shippingOrderAddVisible: boolean = false;
  shippingOrderAddOkLoading: boolean = false;
  addShippingOrder_customerName: string;
  addShippingOrder_allProcessingOrderList: ProcessingOrderVO[] =[];

  addShippingOrder_isLoading: boolean = false;
  addShippingOrder_totalPages: number = 1;
  addShippingOrder_pageIndex: number = 1;
  addShippingOrder_pageSize: number = 2;

  isAllDisplayDataChecked = false;
  isOperating = false;
  isIndeterminate = false;
  listOfDisplayData: ProcessingOrderVO[] = [];
  mapOfCheckedId: { [key: number]: boolean } = {};
  numberOfChecked = 0;
  checkedCustomerId: number = -1;


  constructor(
    private router: Router,
    private shippingOrder: ShippingOrderService,
    private processingOrder: ProcessingOrderService,
    private message: NzMessageService,
    private tab: TabService,
    private fb: FormBuilder
  ) {

  }

  ngOnInit(): void {
    this.search();
  }

  search(): void {
    this.isLoading = true;
    const queryParams: TableQueryParams = {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    };
    this.shippingOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ShippingOrderInfoVO>>) => {
        console.log(res)
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
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
    this.isLoading = false;
  }

  confirmAbandon(id: string): void {
    console.log('confirm abandon: ' + id);
  }

  refresh(): void {
  }

  showAddModal(): void {
    this.shippingOrderAddVisible = true;
    this.resetAddProcessingOrderModal();

    const queryParams: TableQueryParams = {
      pageIndex: this.addShippingOrder_pageIndex,
      pageSize: 1000,
      status: 1, //未完成
    };
    this.processingOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ProcessingOrderVO>>) => {
        console.log('show subscribe');
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        const tableResult: TableResultVO<ProcessingOrderVO> = res.data;

        this.addShippingOrder_totalPages = tableResult.totalPages;
        this.addShippingOrder_pageIndex = tableResult.pageIndex;
        this.addShippingOrder_pageSize = tableResult.pageSize;
        this.addShippingOrder_allProcessingOrderList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  orders: ProcessingOrderVO[] = [];
  ids: number[] = [];
  confirmAdd(): void {
    this.shippingOrderAddOkLoading = true;

    // this.addShippingOrder_allProcessingOrderList.filter(item =>
    //       this.mapOfCheckedId[item.customerId]).map(item => )


    for (let order of this.addShippingOrder_allProcessingOrderList){
      if(this.mapOfCheckedId[order.id]){
        console.log(order.id);
        // ids.push(order.id);
      }
    }
    this.shippingOrderAddOkLoading = false;
    this.shippingOrderAddVisible = false;
    this.message.success('添加成功!');
    this.router.navigateByUrl('/workspace/shipping-order/add')
  }

  cancelAdd(): void {
    this.shippingOrderAddVisible = false;
    this.resetAddProcessingOrderModal();
  }

  searchProcessingOrderBycustomername(): void {
    this.addShippingOrder_isLoading = true;

    this.resetAddProcessingOrderModal();

    const queryParams: TableQueryParams = {
      pageIndex: this.addShippingOrder_pageIndex,
      pageSize: this.addShippingOrder_pageSize,
      customerName: this.addShippingOrder_customerName,
      status: 1, //未完成
    };
    this.processingOrder.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<ProcessingOrderVO>>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        const tableResult: TableResultVO<ProcessingOrderVO> = res.data;

        this.addShippingOrder_totalPages = tableResult.totalPages;
        this.addShippingOrder_pageIndex = tableResult.pageIndex;
        this.addShippingOrder_pageSize = tableResult.pageSize;
        this.addShippingOrder_allProcessingOrderList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });

    this.addShippingOrder_isLoading = false;
  }



  currentPageDataChange($event: ProcessingOrderVO[]): void {
    this.listOfDisplayData = $event;
    this.refreshStatus(null);
  }

  refreshStatus(id: number): void {
    this.isAllDisplayDataChecked = this.listOfDisplayData
      .filter(item => this.isProcessingOrderAvailable(item.customerId))
      .every(item => this.mapOfCheckedId[item.id]);
    this.isIndeterminate =
      this.listOfDisplayData.filter(item => this.isProcessingOrderAvailable(item.customerId))
        .some(item => this.mapOfCheckedId[item.id]) &&
      !this.isAllDisplayDataChecked;
    this.numberOfChecked = this.addShippingOrder_allProcessingOrderList
      .filter(item => this.mapOfCheckedId[item.id]).length;

    if(id !== null){
      if(this.checkedCustomerId === -1){
        this.checkedCustomerId = id;
      } else if(this.numberOfChecked === 0){
        this.checkedCustomerId = -1;
      }
    }

  }

  checkAll(value: boolean): void {
    this.listOfDisplayData.filter(item =>this.isProcessingOrderAvailable(item.customerId))
      .forEach(item => (this.mapOfCheckedId[item.id] = value));
    if(!value){
      this.checkedCustomerId = -1;
    }
    this.refreshStatus(null);
  }

  operateData(): void {
    this.isOperating = true;
    setTimeout(() => {
      this.addShippingOrder_allProcessingOrderList
        .forEach(item => (this.mapOfCheckedId[item.id] = false));
      this.refreshStatus(null);
      this.isOperating = false;
    }, 1000);
  }

  isProcessingOrderAvailable(customerId: number): boolean{
    if(this.checkedCustomerId === -1){
      return true;
    }
    if(customerId === this.checkedCustomerId){
      return true;
    }
    return false;
  }

  resetAddProcessingOrderModal():void{
    this.isAllDisplayDataChecked = false;
    this.isIndeterminate = false;
    this.listOfDisplayData= [];
    this.mapOfCheckedId= {};
    this.numberOfChecked = 0;
    this.checkedCustomerId= -1;
  }
}
