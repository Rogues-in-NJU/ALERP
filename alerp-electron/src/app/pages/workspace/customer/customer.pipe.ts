import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'customer_type_pipe'
})
export class CustomerTypePipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1: return '现金客户';
      default: return '月结客户';
    }
  }

}
