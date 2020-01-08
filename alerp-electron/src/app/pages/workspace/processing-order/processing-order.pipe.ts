import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'processing_order_status'
})
export class ProcessingOrderStatusPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 0: return '草稿中';
      case 1: return '未完成';
      case 2: return '已完成';
      case 3: return '已废弃';
      default: return '已废弃';
    }
  }

}

@Pipe({
  name: 'processing_order_status_color'
})
export class ProcessingOrderStatusColorPipe implements PipeTransform {

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
