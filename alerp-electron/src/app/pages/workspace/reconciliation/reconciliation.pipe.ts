import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'shipping_order_reconciliation_status'
})
export class ShippingOrderReconciliationStatusPipe implements PipeTransform {
  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 0: return '未对账';
      case 1: return '已对账';
      default: return '未对账';
    }
  }

}

@Pipe({
  name: 'shipping_order_reconciliation_status_color'
})
export class ShippingOrderReconciliationStatusColorPipe implements PipeTransform {
  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 0: return 'blue';
      case 1: return 'green';
      default: return 'blue';
    }
  }

}
