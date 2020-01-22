import {Component, OnInit} from "@angular/core";
import {ClosableTab} from "../../tab/tab.component";
import {TabService} from "../../../../core/services/tab.service";
import {Router} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ResultVO} from "../../../../core/model/result-vm";
import {debounceTime} from "rxjs/operators";
import {NzMessageService} from "ng-zorro-antd";
import {HttpErrorResponse} from "@angular/common/http";
import {UserManagementService} from "../../../../core/services/user-management.service";
import {PasswordInfoVO} from "../../../../core/model/password";

@Component({
  selector: 'user-center-password',
  templateUrl: './user-center-password.component.html',
  styleUrls: ['./user-center-password.component.less']
})
export class UserCenterPasswordComponent implements ClosableTab, OnInit {

  userCenterPasswordForm: FormGroup;
  editCache: {_id?: number, data?: TempPurchaseOrderProductInfoVO, product?: TempProductVO, isAdd?: boolean} = {};
  isLoading: boolean = false;

  isSaving: boolean = false;

  constructor(private closeTab: TabService,
              private userManagement: UserManagementService,
              private router: Router,
              private fb: FormBuilder,
              private message: NzMessageService) {

  }

  ngOnInit(): void {
    this.userCenterPasswordForm = this.fb.group({
      oldPassword: [null, Validators.required],
      password1: [null, Validators.required],
      password2: [null, Validators.required],
    });

  }

  updatePassword(): void {
    if (!this.userCenterPasswordForm.valid) {
      return;
    }
    let formData: any = this.userCenterPasswordForm.getRawValue();
    if(formData.password1!=formData.password2){
      this.message.error("两次密码不一致");
      return;
    }
    let passwordUpdate: PasswordInfoVO = {
      oldPassword: formData.oldPassword,
      newPassword: formData.password1
    };
    this.isSaving = true;
    this.userManagement.updatePassword(passwordUpdate)
      .pipe(debounceTime(3000))
      .subscribe((res: ResultVO<any>) => {
        this.message.success(res.data);
        this.isSaving = false;
        // TODO: 跳转回列表页面
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      });
  }

  tabClose(): void {
  }

}

interface TempProductVO {

  id: number,
  name: string
  [key: string]: any;

}

interface TempPurchaseOrderProductInfoVO {

  id?: number;
  name?: string;
  quantity?: number;
  weight?: number;
  price?: number;
  cash?: number;

}
