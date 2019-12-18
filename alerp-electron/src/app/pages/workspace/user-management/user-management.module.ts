import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {UserManagementListComponent} from "./user-management-list/user-management-list.component";
import {AuthorizationGuard} from "../../../guards/authorization.guard";
import {CoreModule} from "../../../core/core.module";
import {SharedModule} from "../../../shared/shared.module";
import {ReactiveFormsModule} from "@angular/forms";
import {UserManagementInfoComponent} from "./user-management-info/user-management-info.component";
import {UserManagementAddComponent} from "./user-management-add/user-management-add.component";
import {UserManagementStatusPipe, UserManagementStatusColorPipe} from "./user-management.pipe";

const routes: Routes = [
  {path: '', pathMatch: 'full', redirectTo: 'list'},
  {
    path: 'list',
    pathMatch: 'full',
    component: UserManagementListComponent,
    canActivate: [AuthorizationGuard],
    data: {
      title: '用户列表',
      removable: true
    }
  }, {
    path: 'add',
    pathMatch: 'full',
    component: UserManagementAddComponent,
    canActivate: [AuthorizationGuard],
    data: {
      title: '新增用户',
      removable: true
    }
  }, {
    path: 'info/:id',
    component: UserManagementInfoComponent,
    canActivate: [AuthorizationGuard],
    data: {
      title: '用户{}',        // title内容将会被显示在tab的标签上，其中通过{}和replaceParams来依次填充route中的特殊内容
      removable: true,
      replaceParams: ['id']
    }
  }
];

@NgModule({
  imports: [
    CoreModule,
    SharedModule,
    RouterModule.forChild(routes),
    ReactiveFormsModule
  ],
  declarations: [
    UserManagementListComponent,
    UserManagementAddComponent,
    UserManagementInfoComponent,
    UserManagementStatusPipe,
    UserManagementStatusColorPipe
  ],
  exports: [RouterModule]
})
export class UserManagementModule {
}
