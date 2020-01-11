import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'purchase_order_status'
})
export class PurchaseOrderStatusPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 0: return '未付款';
      case 1: return '未完成';
      case 2: return '已完成';
      case 3: return '已废弃';
      default: return '已废弃';
    }
  }

}

@Pipe({
  name: 'purchase_order_status_color'
})
export class PurchaseOrderStatusColorPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 0: return 'cyan';
      case 1: return 'blue';
      case 2: return 'green';
      case 3: return '';
      default: return '';
    }
  }

}

@Pipe({
  name: 'purchase_order_price_type'
})
export class PurchaseOrderPriceTypePipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1: return '元/千克';
      default: return '元/件';
    }
  }

}

@Pipe({
  name: 'purchase_order_payment_record'
})
export class PurchaseOrderPaymentRecordPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 0: return '已废弃';
      case 1: return '已确认';
      default: return '已废弃';
    }
  }

}

@Pipe({
  name: 'purchase_order_payment_record_color'
})
export class PurchaseOrderPaymentRecordColorPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 0: return '';
      case 1: return 'green';
      default: return '';
    }
  }

}
