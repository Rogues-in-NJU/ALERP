import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'product_type'
})
export class ProductTypePipe implements PipeTransform{

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 0: return '板材';
      case 1: return '型材';
      case 2: return '铝棒';
      case 3: return '损耗';

    }
  }


}
