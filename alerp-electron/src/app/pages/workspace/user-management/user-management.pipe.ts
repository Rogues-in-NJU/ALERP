import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: 'user_management_status'
})
export class UserManagementStatusPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1:
        return '在职';
      default:
        return '离职';
    }
  }

}

@Pipe({
  name: 'user_management_status_color'
})
export class UserManagementStatusColorPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1:
        return 'green';
      default:
        return 'red';
    }
  }

}

@Pipe({
  name: 'user_management_auth'
})
export class UserManagementAuthPipe implements PipeTransform {

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
  name: 'user_management_auth_color'
})
export class UserManagementAuthColorPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1:
        return 'green';
      default:
        return 'red';
    }
  }

}
