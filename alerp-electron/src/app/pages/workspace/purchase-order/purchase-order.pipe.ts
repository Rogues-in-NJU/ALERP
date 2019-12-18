import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'purchase_order_status'
})
export class PurchaseOrderStatusPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1: return '已完成';
      default: return '已完成';
    }
  }

}

@Pipe({
  name: 'purchase_order_status_color'
})
export class PurchaseOrderStatusColorPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1: return 'green';
      default: return 'green';
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
