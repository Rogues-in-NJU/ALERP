import {Component, OnInit} from "@angular/core";
import {CloseTabEvent, TabService} from "../../../../core/services/tab.service";
import {ActivatedRoute, Router} from "@angular/router";
import {UserManagementInfoVO, userAuthVO} from "../../../../core/model/user-management";
import {ClosableTab} from "../../tab/tab.component";
import {UserManagementService} from "../../../../core/services/user-management.service";
import {ResultVO, ResultCode} from "../../../../core/model/result-vm";
import {HttpErrorResponse} from "@angular/common/http";
import {NzMessageService} from "ng-zorro-antd";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {debounceTime} from "rxjs/internal/operators";
import {Objects} from "../../../../core/services/util.service";

@Component({
  selector: 'user-management-info',
  templateUrl: './user-management-info.component.html',
  styleUrls: ['./user-management-info.component.less']
})
export class UserManagementInfoComponent implements ClosableTab, OnInit {

  userManagementForm: FormGroup;
  isLoading: boolean = true;
  userManagementId: string;
  userManagementData: UserManagementInfoVO;
  isSaving: boolean = false;
  authlist: userAuthVO[] = [];

  constructor(private closeTabService: TabService,
              private route: ActivatedRoute,
              private router: Router,
              private fb: FormBuilder,
              private userManagement: UserManagementService,
              private message: NzMessageService) {

  }

  ngOnInit(): void {
    this.search();
  }

  search(): void {
    this.userManagementForm = this.fb.group({
      name: [null],
      phoneNumber: [null],
    });
    this.userManagementId = this.route.snapshot.params['id'];
    this.userManagement.find(this.userManagementId)
      .subscribe((res: ResultVO<UserManagementInfoVO>) => {
        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.isLoading = false;
        this.userManagementData = res.data;
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      });
  }

  confirmAdd(id: number): void {
    let userInfo: UserManagementInfoVO = this.userManagementData;
    for (const tmp of userInfo.authList1) {
      if (tmp.authId == id) {
        tmp.action = 1;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList2) {
      if (tmp.authId == id) {
        tmp.action = 1;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList3) {
      if (tmp.authId == id) {
        tmp.action = 1;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList4) {
      if (tmp.authId == id) {
        tmp.action = 1;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList5) {
      if (tmp.authId == id) {
        tmp.action = 1;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList6) {
      if (tmp.authId == id) {
        tmp.action = 1;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList7) {
      if (tmp.authId == id) {
        tmp.action = 1;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList8) {
      if (tmp.authId == id) {
        tmp.action = 1;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList9) {
      if (tmp.authId == id) {
        tmp.action = 1;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList10) {
      if (tmp.authId == id) {
        tmp.action = 1;
      }
      this.authlist.push(tmp);
    }

    this.isSaving = true;
    userInfo.authList = this.authlist;
    this.userManagement.save(userInfo)
      .subscribe((res: ResultVO<any>) => {
        this.message.success(res.message);
        this.isSaving = false;
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
        this.refresh();
      }, () => {
        this.refresh();
      });
  }

  confirmAbandon(id: number): void {
    let userInfo: UserManagementInfoVO = this.userManagementData;
    for (const tmp of userInfo.authList1) {
      if (tmp.authId == id) {
        tmp.action = 0;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList2) {
      if (tmp.authId == id) {
        tmp.action = 0;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList3) {
      if (tmp.authId == id) {
        tmp.action = 0;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList4) {
      if (tmp.authId == id) {
        tmp.action = 0;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList5) {
      if (tmp.authId == id) {
        tmp.action = 0;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList6) {
      if (tmp.authId == id) {
        tmp.action = 0;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList7) {
      if (tmp.authId == id) {
        tmp.action = 0;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList8) {
      if (tmp.authId == id) {
        tmp.action = 0;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList9) {
      if (tmp.authId == id) {
        tmp.action = 0;
      }
      this.authlist.push(tmp);
    }
    for (const tmp of userInfo.authList10) {
      if (tmp.authId == id) {
        tmp.action = 0;
      }
      this.authlist.push(tmp);
    }

    this.isSaving = true;
    userInfo.authList = this.authlist;
    this.userManagement.save(userInfo)
      .subscribe((res: ResultVO<any>) => {
        this.message.success(res.message);
        this.isSaving = false;
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
        this.refresh();
      }, () => {
        this.refresh();
      });
  }

  tabClose(): void {
    this.closeTabService.closeEvent.emit({
      url: this.router.url,
      refreshUrl: null
    });
  }

  refresh(): void {
    this.search();
  }

}
