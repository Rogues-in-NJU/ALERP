import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {UserCenterInfoComponent} from "./user-center-info/user-center-info.component";
import {CoreModule} from "../../../core/core.module";
import {SharedModule} from "../../../shared/shared.module";
import {UserCenterAuthPipe, UserCenterAuthColorPipe} from "./user-center.pipe";
import {UserCenterPasswordComponent} from "./user-center-password/user-center-password.component";
import {ReactiveFormsModule} from "@angular/forms";

const routes: Routes = [
  {path: '', pathMatch: 'full', redirectTo: 'user'},
  {
    path: 'user',
    component: UserCenterInfoComponent,
    data: {
      title: '用户信息',
      removable: true
    }
  },
  {
    path: 'password',
    component: UserCenterPasswordComponent,
    data: {
      title: '修改密码',
      removable: true
    }
  }
];

@NgModule({
  providers: [],
  imports: [CoreModule, SharedModule, RouterModule.forChild(routes),ReactiveFormsModule],
  declarations: [
    UserCenterInfoComponent,
    UserCenterPasswordComponent,
    UserCenterAuthPipe,
    UserCenterAuthColorPipe
  ],
  exports: [RouterModule]
})
export class UserCenterModule {

}
