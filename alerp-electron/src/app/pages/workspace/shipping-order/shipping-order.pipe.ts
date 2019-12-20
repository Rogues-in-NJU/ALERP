import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'shipping_order_status'
})
export class ShippingOrderStatusPipe implements PipeTransform {
  transform(value: any, ...args: any[]): any {
  }

}
