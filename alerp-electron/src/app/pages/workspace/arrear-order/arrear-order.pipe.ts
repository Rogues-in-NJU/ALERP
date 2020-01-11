import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'arrear_order_status'
})
export class ArrearOrderStatusPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1: return '未收款';
      case 2: return '部分收款';
      case 3: return '已完成';
      case 4: return '已废弃';
      default: return '未收款';
    }
  }

}

@Pipe({
  name: 'arrear_order_status_color'
})
export class ArrearOrderStatusColorPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1: return 'green';
      default: return 'green';
    }
  }

}
