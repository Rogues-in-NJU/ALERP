import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {AuthorizationGuard} from "../../../guards/authorization.guard";
import {CoreModule} from "../../../core/core.module";
import {SharedModule} from "../../../shared/shared.module";
import {ReactiveFormsModule} from "@angular/forms";
import {ExpenseListComponent} from "./expense-list/expense-list.component";
import {ExpenseAddComponent} from "./expense-add/expense-add.component";

const routes: Routes = [
  {path: '', pathMatch: 'full', redirectTo: 'list'},
  {
    path: 'list',
    pathMatch: 'full',
    component: ExpenseListComponent,
    canActivate: [AuthorizationGuard],
    data: {
      title: '公司支出列表',
      removable: true
    }
  }, {
    path: 'add',
    pathMatch: 'full',
    component: ExpenseAddComponent,
    canActivate: [AuthorizationGuard],
    data: {
      title: '新增公司支出',
      removable: true
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
    ExpenseListComponent,
    ExpenseAddComponent
  ],
  exports: [RouterModule]
})
export class ExpenseModule {
}
