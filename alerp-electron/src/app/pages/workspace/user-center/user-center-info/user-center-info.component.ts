import {Component} from "@angular/core";
import {FormGroup, FormBuilder} from "@angular/forms";
import {UserManagementInfoVO} from "../../../../core/model/user-management";
import {TabService} from "../../../../core/services/tab.service";
import {ActivatedRoute, Router} from "@angular/router";
import {UserManagementService} from "../../../../core/services/user-management.service";
import {NzMessageService} from "ng-zorro-antd";
import {HttpErrorResponse} from "@angular/common/http";
import {ResultVO, ResultCode} from "../../../../core/model/result-vm";
import {debounceTime} from "rxjs/internal/operators";
import {Objects} from "../../../../core/services/util.service";

@Component({
  selector: 'user-center-info',
  templateUrl: './user-center-info.component.html',
  styleUrls: ['./user-center-info.component.less']
})
export class UserCenterInfoComponent {
  userNameForm: FormGroup;
  userPhoneNumberForm: FormGroup;
  isLoading: boolean = true;
  userManagementData: UserManagementInfoVO;
  isSaving: boolean = false;



  isVisibleName = false;
  isVisiblePhoneNumber = false;

  showModalName(): void {
    this.isVisibleName = true;
  }

  handleCancelName(): void {
    this.isVisibleName = false;
  }

  showModalPhoneNumber(): void {
    this.isVisiblePhoneNumber = true;
  }

  handleCancelPhoneNumber(): void {
    this.isVisiblePhoneNumber = false;
  }

  constructor(private closeTabService: TabService,
              private route: ActivatedRoute,
              private router: Router,
              private fb: FormBuilder,
              private userManagement: UserManagementService,
              private message: NzMessageService) {

  }

  ngOnInit(): void {
    this.userNameForm = this.fb.group({
      name: [null]
    });
    this.userPhoneNumberForm = this.fb.group({
      phoneNumber: [null]
    });
    this.userManagement.findSelf()
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
        // console.log(this.userManagementData);
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  saveUserName(): void {
    if (!this.userNameForm.valid) {
      return;
    }
    let formData: any = this.userNameForm.getRawValue();
    let userManagementAdd: UserManagementInfoVO = this.userManagementData;
    userManagementAdd.name = formData.name;
    this.isSaving = true;
    this.userManagement.save(userManagementAdd)
      .pipe(debounceTime(3000))
      .subscribe((res: ResultVO<any>) => {
        this.message.success('修改成功!');
        this.isSaving = false;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
    this.isVisibleName = false;
  }

  saveUserPhoneNumber(): void {
    if (!this.userPhoneNumberForm.valid) {
      return;
    }
    let formData: any = this.userPhoneNumberForm.getRawValue();
    let userManagementAdd: UserManagementInfoVO = this.userManagementData;
    userManagementAdd.phoneNumber = formData.phoneNumber;
    this.isSaving = true;
    this.userManagement.save(userManagementAdd)
      .pipe(debounceTime(3000))
      .subscribe((res: ResultVO<any>) => {
        this.message.success('修改成功!');
        this.isSaving = false;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
    this.isVisiblePhoneNumber = false;
  }

  tabClose(): void {
    this.closeTabService.closeEvent.emit({
      url: this.router.url,
      refreshUrl: null
    });
  }
}
