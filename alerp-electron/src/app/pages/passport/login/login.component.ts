import { Component, OnInit } from "@angular/core";
import { AuthService, UserService } from "../../../core/services/user.service";
import { NzMessageService } from "ng-zorro-antd";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";

@Component({
  selector: 'passport-login',
  templateUrl: './login.component.html',
  styleUrls: [ './login.component.less' ]
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;

  constructor(
    private user: UserService,
    private auth: AuthService,
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
      username: [ null, [ Validators.required, Validators.pattern(/^1[3456789]\d{9}$/) ] ],
      password: [ null, Validators.required ],
      location: [ 1, Validators.required ]
    });
  }

  login(): void {
    this.router.navigate(['/workspace']);
    // if (!this.loginForm.valid) {
    //   Object.values(this.loginForm.controls).forEach(c => {
    //     c.markAsDirty();
    //     c.updateValueAndValidity();
    //   });
    // }
  }

}
