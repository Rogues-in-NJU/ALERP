import { Component, OnInit } from "@angular/core";
import { UserService } from "../../../core/services/user.service";
import { NzMessageService } from "ng-zorro-antd";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { LoginCode, ResultCode, ResultVO } from "../../../core/model/result-vm";
import { LoginResultVO } from "../../../core/model/user";
import { HttpErrorResponse } from "@angular/common/http";
import { Objects } from "../../../core/services/util.service";

@Component({
  selector: 'passport-login',
  templateUrl: './login.component.html',
  styleUrls: [ './login.component.less' ]
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  isLoading: boolean = false;

  constructor(
    private user: UserService,
    private message: NzMessageService,
    private fb: FormBuilder,
    private router: Router
  ) {
  }

  forgetPassword(): void {
    this.message.info('请咨询管理员重置密码！');
  }

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      phoneNumber: [ null, [ Validators.required, Validators.pattern(/^1[3456789]\d{9}$/) ] ],
      password: [ null, Validators.required ],
      city: [ 1, Validators.required ]
    });
  }

  login(): void {
    if (!this.loginForm.valid) {
      Object.values(this.loginForm.controls).forEach(item => {
        item.markAsDirty();
        item.updateValueAndValidity();
      });
      return;
    }
    this.isLoading = true;
    this.user.login(this.loginForm.getRawValue())
      .subscribe((res: ResultVO<LoginResultVO>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        const loginVO: LoginResultVO = res.data;
        if (loginVO.code === LoginCode.SUCCESS.code) {
          this.router.navigate(['/workspace']);
        } else {
          this.message.error(loginVO.result);
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
        this.isLoading = false;
      }, () => {
        this.isLoading = false;
      });
  }

}
