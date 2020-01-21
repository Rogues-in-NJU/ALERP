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

  userName: string;

  constructor(
    private storage: LocalStorageService,
    private user: UserService,
    private userManagement: UserManagementService,
    private route: Router
  ) {
  }

  ngOnInit(): void {
    const userInfo: UserStorageVO = this.storage.getObject<UserStorageVO>('user');
    this.city = userInfo.city;
    this.userManagement.findSelf()
      .subscribe((res: ResultVO<UserManagementInfoVO>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        this.userName = res.data.name;
      }, (error: HttpErrorResponse) => {

      }, () => {

      });
  }

  logout(): void {
    this.storage.remove('user');
    this.isOpen = false;
    setTimeout(() => {
      SimpleReuseStrategy.reset();
      this.tabComponent.resetMenu();
      this.user.logout()
        .subscribe((res: ResultVO<any>) => {}, (error: HttpErrorResponse) => {
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
    switch(value) {
      case 1: return '苏州';
      case 2: return '昆山';
      default: return '苏州';
    }
  }

}
