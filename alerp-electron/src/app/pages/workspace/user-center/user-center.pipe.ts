import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: 'user_center_auth'
})
export class UserCenterAuthPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1:
        return '拥有';
      default:
        return '未拥有';
    }
  }

}

@Pipe({
  name: 'user_center_auth_color'
})
export class UserCenterAuthColorPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1:
        return 'green';
      default:
        return 'red';
    }
  }

}
