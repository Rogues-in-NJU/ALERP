import { Component, OnInit } from "@angular/core";
import { CloseTabEvent, TabService } from "../../../../core/services/tab.service";
import { ActivatedRoute, Router } from "@angular/router";
import { PurchaseOrderPaymentRecordVO, PurchaseOrderVO } from "../../../../core/model/purchase-order";
import { ClosableTab, RefreshableTab } from "../../tab/tab.component";
import { PurchaseOrderService } from "../../../../core/services/purchase-order.service";
import { ResultCode, ResultVO } from "../../../../core/model/result-vm";
import { HttpErrorResponse } from "@angular/common/http";
import { NzMessageService } from "ng-zorro-antd";
import { DateUtils, Objects, StringUtils } from "../../../../core/services/util.service";

@Component({
  selector: 'purchase-order-info',
  templateUrl: './purchase-order-info.component.html',
  styleUrls: [ './purchase-order-info.component.less' ]
})
export class PurchaseOrderInfoComponent implements RefreshableTab, OnInit {

  isLoading: boolean = true;
  purchaseOrderId: number;
  purchaseOrderData: PurchaseOrderVO;

  editCache: {
    _id?: number,
    data?: TempPurchaseOrderPaymentRecordVO,
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
  purchaseOrderPaymentRecordCountIndex: number = 0;

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
    private tab: TabService,
    private route: ActivatedRoute,
    private router: Router,
    private purchaseOrder: PurchaseOrderService,
    private message: NzMessageService
  ) {

  }

  ngOnInit(): void {
    this.purchaseOrderId = this.route.snapshot.params[ 'id' ];
    this.refresh();
  }

  addPaymentRecordRow(): void {
    if (Objects.valid(this.editCache._id)) {
      this.message.warning('请先保存付款记录列表的更改!');
      return;
    }
    let item: PurchaseOrderPaymentRecordVO = {
      id: null,
      purchaseOrderId: this.purchaseOrderData.id,
      cash: null,
      description: null,
      salesman: null,
      doneAt: null,
      updatedAt: this.purchaseOrderData.updatedAt
    };
    item[ '_id' ] = this.purchaseOrderPaymentRecordCountIndex++;
    this.purchaseOrderData.paymentRecords = [
      item,
      ...this.purchaseOrderData.paymentRecords
    ];
    this.editCache._id = item[ '_id' ];
    this.editCache.data = {};
    Object.assign(this.editCache.data, item);
    this.editCache.isAdd = true;
  }

  confirmPaymentRecordDelete(id: number): void {
      this.purchaseOrder.deletePaymentRecord(id)
        .subscribe((res: ResultVO<any>) => {
          if (!Objects.valid(res)) {
            this.message.error('修改失败!');
            return;
          }
          if (res.code !== ResultCode.SUCCESS.code) {
            this.message.error(res.message);
            return;
          }
          this.message.success('修改成功!');
        }, (error: HttpErrorResponse) => {
          this.message.error(error.message);
          this.refresh();
        }, () => {
          this.refresh();
        });
  }

  cancelPaymentRecordDelete(_id: number): void {
    if (Objects.valid(this.editCache.isAdd) && this.editCache.isAdd) {
      this.purchaseOrderData.paymentRecords = this.purchaseOrderData.paymentRecords.filter(item => item['_id'] !== _id);
    }
    Object.assign(this.editCache, this.defaultEditCache);
  }

  savePaymentEdit(_id: number): void {
    if (_id !== this.editCache._id) {
      return;
    }
    if (!this.checkPurchaseOrderPaymentRecordValid()) {
      return;
    }
    const index = this.purchaseOrderData.paymentRecords.findIndex(item => item['_id'] === _id);
    Object.assign(this.purchaseOrderData.paymentRecords[index], this.editCache.data, {
      doneAt: DateUtils.format(new Date(this.editCache.data.doneAt))
    });
    Object.assign(this.editCache.data, this.defaultEditCache);
    this.purchaseOrder.savePaymentRecord(this.purchaseOrderData.paymentRecords[index])
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        this.message.success('修改成功!');
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
        this.refresh();
      }, () => {
        this.refresh();
      });
  }

  refresh(): void {
    Object.assign(this.editCache, this.defaultEditCache);
    this.purchaseOrder.find(this.purchaseOrderId)
      .subscribe((res: ResultVO<PurchaseOrderVO>) => {
        if (!Objects.valid(res)) {
          return;
        }
        this.purchaseOrderData = res.data;
        if (Objects.valid(this.purchaseOrderData.paymentRecords)) {
          this.purchaseOrderData.paymentRecords.forEach(item => {
            item[ '_id' ] = this.purchaseOrderPaymentRecordCountIndex++;
            item.purchaseOrderId = this.purchaseOrderData.id;
            item.updatedAt = this.purchaseOrderData.updatedAt;
          });
        }
        console.log(this.purchaseOrderData);
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      }, () => {
        this.isLoading = false;
      });
  }

  checkCashNotNull(): boolean {
    if (!Objects.isNaN(this.editCache.data.cash)) {
      this.editCacheValidateStatus.cash = null;
      return true;
    } else {
      this.editCacheValidateStatus.cash = 'error';
      return false;
    }
  }

  checkDoneAtNotNull(): boolean {
    if (!Objects.isNaN(this.editCache.data.doneAt)) {
      this.editCacheValidateStatus.doneAt = null;
      return true;
    } else {
      this.editCacheValidateStatus.doneAt = 'error';
      return false;
    }
  }

  checkPurchaseOrderPaymentRecordValid(): boolean {
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

}

interface TempPurchaseOrderPaymentRecordVO {

  cash?: number,
  description?: string,
  salesman?: string,
  doneAt?: string

}
