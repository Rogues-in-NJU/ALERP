import {Component, OnInit} from "@angular/core";
import {CloseTabEvent, TabService} from "../../../../core/services/tab.service";
import {ActivatedRoute, Router} from "@angular/router";
import {UserManagementInfoVO} from "../../../../core/model/user-management";
import {ClosableTab} from "../../tab/tab.component";
import {UserManagementService} from "../../../../core/services/user-management.service";
import {ResultVO} from "../../../../core/model/result-vm";
import {HttpErrorResponse} from "@angular/common/http";
import {NzMessageService} from "ng-zorro-antd";

@Component({
  selector: 'user-management-info',
  templateUrl: './user-management-info.component.html',
  styleUrls: ['./user-management-info.component.less']
})
export class UserManagementInfoComponent implements ClosableTab, OnInit {

  isLoading: boolean = true;
  userManagementId: string;
  userManagementData: UserManagementInfoVO;

  constructor(private closeTabService: TabService,
              private route: ActivatedRoute,
              private router: Router,
              private userManagement: UserManagementService,
              private message: NzMessageService) {

  }

  ngOnInit(): void {
    this.userManagementId = this.route.snapshot.params['id'];
    this.userManagement.find(this.userManagementId)
      .subscribe((res: ResultVO<UserManagementInfoVO>) => {
        if (!res) {
          return;
        }
        this.isLoading = false;
        this.userManagementData = res.data;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  tabClose(): void {
    this.closeTabService.closeEvent.emit({
      url: this.router.url,
      refreshUrl: null
    });
  }

}
