import {Component, OnInit} from "@angular/core";
import {CloseTabEvent, TabService} from "../../../../core/services/tab.service";
import {ActivatedRoute, Router} from "@angular/router";
import {UserManagementInfoVO} from "../../../../core/model/user-management";
import {ClosableTab} from "../../tab/tab.component";
import {UserManagementService} from "../../../../core/services/user-management.service";
import {ResultVO} from "../../../../core/model/result-vm";
import {HttpErrorResponse} from "@angular/common/http";
import {NzMessageService} from "ng-zorro-antd";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {debounceTime} from "rxjs/internal/operators";

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
        if (!res) {
          return;
        }
        this.isLoading = false;
        this.userManagementData = res.data;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  confirmAdd(id: number): void {
    let userInfo: UserManagementInfoVO = this.userManagementData;
    for (const tmp of userInfo.authList) {
      if (tmp.authId == id) {
        tmp.action = 1;
      }
    }
    this.isSaving = true;
    this.userManagement.save(userInfo)
      .pipe(debounceTime(3000))
      .subscribe((res: ResultVO<any>) => {
        this.message.success(res.message);
        this.isSaving = false;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  confirmAbandon(id: number): void {
    let userInfo: UserManagementInfoVO = this.userManagementData;
    for (const tmp of userInfo.authList) {
      if (tmp.authId == id) {
        tmp.action = 0;
      }
    }
    this.isSaving = true;
    this.userManagement.save(userInfo)
      .pipe(debounceTime(3000))
      .subscribe((res: ResultVO<any>) => {
        this.message.success(res.message);
        this.isSaving = false;
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

  refresh(): void {
    this.search();
  }

}
