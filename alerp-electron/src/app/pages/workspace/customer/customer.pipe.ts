import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'customer_type_pipe'
})
export class CustomerTypePipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1: return '现金客户';
      case 2: return '月结客户';
      default: return '月结客户';
    }
  }

}

@Pipe({
  name: 'customer_price_type_pipe'
})
export class CustomerPriceTypePipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1: return '元/千克';
      case 2: return '元/件';
      default: return '元/件';
    }
  }

}
