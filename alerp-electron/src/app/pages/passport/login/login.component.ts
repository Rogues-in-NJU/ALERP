import { Component } from "@angular/core";
import { AuthService, UserService } from "../../../core/services/user.service";
import { NzMessageService } from "ng-zorro-antd";

@Component({
  selector: 'passport-login',
  templateUrl: './login.component.html',
  styleUrls: [ './login.component.less' ]
})
export class LoginComponent {

  constructor(
    private user: UserService,
    private auth: AuthService,
    private message: NzMessageService
  ) {
  }

  forgetPassword(): void {
    this.message.info('请咨询管理员重置密码！');
  }

}
