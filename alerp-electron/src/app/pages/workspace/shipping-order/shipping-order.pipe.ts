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
      case 0: return 'blue';
      case 1: return 'green';
      case 2: return '';
      default: return '';
    }
  }

}

@Pipe({
  name: 'shipping_order_price_type'
})
export class ShippingOrderPriceTypePipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1: return '元/公斤';
      case 2: return '元/件';
      default: return '';
    }
  }

}

@Pipe({
  name: 'shipping_order_hasTax_type'
})
export class ShippingOrderHasTaxTypePipe implements PipeTransform {

  transform(value: boolean, ...args: any[]): any {
    switch (value) {
      case true: return '含税';
      case false: return '不含税';
      default: return '';
    }
  }

}
