import { Component, OnInit, Pipe, PipeTransform } from "@angular/core";
import { LocalStorageService } from "../../core/services/local-storage.service";
import { UserService } from "../../core/services/user.service";
import { Router } from "@angular/router";
import { UserStorageVO } from "../../core/model/user";

@Component({
  selector: 'app-workspace',
  templateUrl: './workspace.component.html',
  styleUrls: [ './workspace.component.less' ]
})
export class WorkspaceComponent implements OnInit {

  isCollapsed: boolean = false;
  isOpen: boolean = false;

  city: number;

  constructor(
    private storage: LocalStorageService,
    private user: UserService,
    private route: Router
  ) {
  }

  ngOnInit(): void {
    const userInfo: UserStorageVO = this.storage.getObject<UserStorageVO>('user');
    this.city = userInfo.city;
  }

  logout(): void {
    this.storage.remove('user');
    this.isOpen = false;
    setTimeout(() => {
      this.route.navigate([ '/passport/login' ]);
    }, 1000);
  }

}

@Pipe({
  name: 'city'
})
export class CityPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch(value) {
      case 1: return '苏州';
      case 2: return '昆山';
      default: return '苏州';
    }
  }

}
