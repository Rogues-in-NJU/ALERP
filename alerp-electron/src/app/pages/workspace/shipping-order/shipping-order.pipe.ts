import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'shipping_order_status'
})
export class ShippingOrderStatusPipe implements PipeTransform {
  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 0: return '已出货';
      case 1: return '已完成';
      case 2: return '已废弃';
      default: return '已完成';
    }
  }

}

@Pipe({
  name: 'shipping_order_status_color'
})
export class ShippingOrderStatusColorPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1: return 'green';
      default: return 'green';
    }
  }

}

@Pipe({
  name: 'shipping_order_price_type'
})
export class ShippingOrderPriceTypePipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1: return '元/千克';
      case 2: return '元/件';
      default: return '';
    }
  }

}
