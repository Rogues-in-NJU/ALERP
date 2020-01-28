import { Component, OnInit, Pipe, PipeTransform, ViewChild } from "@angular/core";
import { LocalStorageService } from "../../core/services/local-storage.service";
import { UserService } from "../../core/services/user.service";
import { Router } from "@angular/router";
import { UserStorageVO } from "../../core/model/user";
import { ResultCode, ResultVO } from "../../core/model/result-vm";
import { HttpErrorResponse } from "@angular/common/http";
import { SimpleReuseStrategy } from "../../core/strategy/simple-reuse.strategy";
import { TabComponent } from "./tab/tab.component";
import { UserManagementService } from "../../core/services/user-management.service";
import { UserManagementInfoVO } from "../../core/model/user-management";
import { Objects } from "../../core/services/util.service";
import { CookieService } from "ngx-cookie";
import { NzNotificationService } from "ng-zorro-antd";
import { AppConfig } from "../../../environments/environment";

@Component({
  selector: 'app-workspace',
  templateUrl: './workspace.component.html',
  styleUrls: [ './workspace.component.less' ]
})
export class WorkspaceComponent implements OnInit {

  @ViewChild("tabComponent", null) tabComponent: TabComponent;

  isCollapsed: boolean = false;
  isOpen: boolean = false;

  city: number;

  constructor(
    private storage: LocalStorageService,
    private user: UserService,
    private route: Router,
    private cookie: CookieService,
    private notification: NzNotificationService
  ) {
  }

  ngOnInit(): void {
    const userInfo: UserStorageVO = this.storage.getObject<UserStorageVO>('user');
    this.city = userInfo.city;

    setInterval(() => {
      if (Objects.valid(this.storage.get('user'))) {
        this.user.isSessionValidate()
          .subscribe((res: ResultVO<boolean>) => {
            if (!Objects.valid(res)) {
              this.notification.warning('会话失效', '登录状态失效，请重新登录!', { nzDuration: 0 });
              this.logout();
              return;
            }
            if (res.code !== ResultCode.SUCCESS.code) {
              this.notification.warning('会话失效', '登录状态失效，请重新登录!', { nzDuration: 0 });
              this.logout();
              return;
            }
            if (!res.data) {
              this.notification.warning('会话失效', '登录状态失效，请重新登录!', { nzDuration: 0 });
              this.logout();
            }
          }, (error: HttpErrorResponse) => {
            this.notification.warning('会话失效', '登录状态失效，请重新登录!', { nzDuration: 0 });
            this.logout();
          });
      }
    }, 10000);
  }

  logout(): void {
    this.storage.remove('user');
    this.isOpen = false;
    setTimeout(() => {
      SimpleReuseStrategy.reset();
      this.tabComponent.resetMenu();
      this.cookie.removeAll();
      this.user.logout()
        .subscribe((res: ResultVO<any>) => {
        }, (error: HttpErrorResponse) => {
          this.route.navigate([ '/passport/login' ]);
        }, () => {
          this.route.navigate([ '/passport/login' ]);
        });
    }, 1000);
  }

}

@Pipe({
  name: 'city'
})
export class CityPipe implements PipeTransform {

  transform(value: number, ...args: any[]): any {
    switch (value) {
      case 1:
        return '苏州';
      case 2:
        return '昆山';
      default:
        return '苏州';
    }
  }

}
