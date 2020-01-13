import {Component, OnInit} from "@angular/core";
import {RefreshableTab} from "../../tab/tab.component";
import {PurchaseOrderPaymentRecordVO, PurchaseOrderVO} from "../../../../core/model/purchase-order";
import {ArrearOrderInfoVO, ArrearOrderReceiptRecordVO} from "../../../../core/model/arrear-order";
import {DateUtils, Objects} from "../../../../core/services/util.service";
import {ActivatedRoute, Router} from "@angular/router";
import {PurchaseOrderService} from "../../../../core/services/purchase-order.service";
import {NzMessageService} from "ng-zorro-antd";
import {ArrearOrderService} from "../../../../core/services/arrear-order.service";
import {QueryParams, ResultCode, ResultVO, TableResultVO} from "../../../../core/model/result-vm";
import {HttpErrorResponse} from "@angular/common/http";
import {ProcessingOrderVO} from "../../../../core/model/processing-order";

@Component({
  selector: 'arrear-order-info',
  templateUrl: './arrear-order-info.component.html',
  styleUrls: [ './arrear-order-info.component.less' ]
})
export class ArrearOrderInfoComponent implements RefreshableTab, OnInit {

  //todo duedate invoicenumber 可修改

  isLoading: boolean = true;
  arrearOrderId: number;
  arrearOrderData: ArrearOrderInfoVO;

  //修改
  duedate: string;
  isChangeDueDate: boolean = false;
  invoiceNumber: string;
  isChangeInvoiceNumber: boolean = false;

  editCache: {
    _id?: number,
    data?: TempArrearOrderReceiptRecordVO,
    isAdd?: boolean
  } = {};

  defaultEditCache: any = {
    _id: null,
    data: null,
    isAdd: false
  };

  editCacheValidateStatus: any = {
    cash: null,
    doneAt: null
  };
  arrearOrderReceiptRecordCountIndex: number = 0;

  cashFormatter: any = (value: number) => {
    if (Objects.valid(value)) {
      return `¥ ${value}`;
    } else {
      return `¥`;
    }
  };
  disabledDate: any = (current: Date): boolean => {
    return DateUtils.compare(current, new Date()) > 0;
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private arrearOrder: ArrearOrderService,
    private message: NzMessageService
  ) {
  }
  ngOnInit(): void {
    this.arrearOrderId = this.route.snapshot.params['id'];
    Object.assign(this.editCache, this.defaultEditCache);
    this.refresh();

  }

  refresh(): void {
    Object.assign(this.editCache, this.defaultEditCache);
    console.log(this.arrearOrderId);
    this.arrearOrder.find(this.arrearOrderId)
      .subscribe((res: ResultVO<ArrearOrderInfoVO>) => {
        console.log(res)
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        this.isLoading = false;
        this.arrearOrderData = res.data;
        this.invoiceNumber = res.data.invoiceNumber;
        this.duedate = res.data.dueDate;
        if (Objects.valid(this.arrearOrderData.receiptRecordList)) {
          this.arrearOrderData.receiptRecordList.forEach(item => {
            item[ '_id' ] = this.arrearOrderReceiptRecordCountIndex++;
          })
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  addReceiptRecordRow(): void {
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存收款记录列表的更改!');
      return;
    }
    let item: ArrearOrderReceiptRecordVO = {
      id: null,
      arrearOrderId: this.arrearOrderId,
      cash: null,
      description: null,
      salesman: null,
      doneAt: null
    };
    item[ '_id' ] = this.arrearOrderReceiptRecordCountIndex++;
    this.arrearOrderData.receiptRecordList = [
      item,
      ...this.arrearOrderData.receiptRecordList
    ];
    this.editCache._id = item[ '_id' ];
    this.editCache.data = {};
    Object.assign(this.editCache.data, item);
    this.editCache.isAdd = true;
  }


  confirmPaymentRecordDelete(_id: number): void {
    // TODO: 提交修改
    this.arrearOrderData.receiptRecordList = this.arrearOrderData.receiptRecordList.filter(item => item['_id'] !== _id);
  }

  cancelPaymentRecordDelete(_id: number): void {
    if (Objects.valid(this.editCache.isAdd) && this.editCache.isAdd) {
      this.arrearOrderData.receiptRecordList = this.arrearOrderData.receiptRecordList.filter(item => item['_id'] !== _id);
    }
    Object.assign(this.editCache, this.defaultEditCache);
  }

  saveReceiptsEdit(_id: number): void {
    if (_id !== this.editCache._id) {
      return;
    }
    if (!this.checkArrearOrderReceiptRecordValid()) {
      return;
    }
    const index = this.arrearOrderData.receiptRecordList.findIndex(item => item['_id']=== _id);
    //todo null
    Object.assign(this.arrearOrderData.receiptRecordList[index], this.editCache.data, {
      doneAt: DateUtils.format(new Date(this.editCache.data.doneAt))
    });
    Object.assign(this.editCache.data, this.defaultEditCache);
    console.log(this.arrearOrderData.receiptRecordList[index]);
    this.arrearOrder.saveReceiptRecord(this.arrearOrderData.receiptRecordList[index])
      .subscribe((res: ResultVO<any>) => {
        console.log(res);
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        this.message.success('新增成功!');
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      }, () => {
        this.refresh();
      });
  }

  checkArrearOrderReceiptRecordValid(): boolean {
    if (!Objects.valid(this.editCache.data)) {
      return false;
    }
    let isValid: boolean = true;
    if (!this.checkCashNotNull()) {
      this.editCacheValidateStatus.cash = 'error';
      isValid = false;
    }
    if (!this.checkDoneAtNotNull()) {
      this.editCacheValidateStatus.doneAt = 'error';
      isValid = false;
    }
    return isValid;
  }

  checkCashNotNull(): boolean {
    if (Objects.valid(this.editCache.data.cash)) {
      this.editCacheValidateStatus.cash = null;
      return true;
    } else {
      this.editCacheValidateStatus.cash = 'error';
      return false;
    }
  }

  checkDoneAtNotNull(): boolean {
    if (Objects.valid(this.editCache.data.doneAt)) {
      this.editCacheValidateStatus.doneAt = null;
      return true;
    } else {
      this.editCacheValidateStatus.doneAt = 'error';
      return false;
    }
  }

  changeDueDate(): void{
    this.isChangeDueDate = true;
  }

  confirmChangeDueDate(): void{
    let queryParams: QueryParams = {};
    queryParams['id'] = this.arrearOrderId;
    queryParams['dueDate'] = DateUtils.format(new Date(this.duedate));
    queryParams['updatedAt'] = this.arrearOrderData.updatedAt;

    this.arrearOrder.changeDueDate(queryParams)
      .subscribe((res: ResultVO<TableResultVO<any>>) => {
        console.log(res);

        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.message.success("修改成功");
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      },()=>{
        this.isChangeInvoiceNumber = false;
        this.refresh();
      });
  }

  cancelChangeDueDate(): void{
    this.duedate = null;
    this.isChangeDueDate = false;
  }

  changeInvoiceNumber(): void{
    this.isChangeInvoiceNumber = true;
  }

  confirmChangeInvoiceNumber(): void{
    let queryParams: QueryParams = {};
    queryParams['id'] = this.arrearOrderId;
    queryParams['invoiceNumber'] = this.invoiceNumber;
    queryParams['updatedAt'] = this.arrearOrderData.updatedAt;

    this.arrearOrder.changeInvoiceNumber(queryParams)
      .subscribe((res: ResultVO<TableResultVO<any>>) => {
        console.log(res);

        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.message.success("修改成功");
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      },()=>{
        this.isChangeInvoiceNumber = false;
        this.refresh();
    });
  }

  cancelChangeInvoiceNumber(): void{
    this.invoiceNumber = null;
    this.isChangeInvoiceNumber = false;
  }

}

interface TempArrearOrderReceiptRecordVO {

  cash?: number,
  description?: string,
  salesman?: string,
  doneAt?: string

}

