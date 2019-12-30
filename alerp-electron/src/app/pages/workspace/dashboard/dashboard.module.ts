import { NgModule, OnInit } from "@angular/core";
import { DashboardComponent } from "./dashboard.component";
import { CoreModule } from "../../../core/core.module";
import { SharedModule } from "../../../shared/shared.module";
import { RouterModule, Routes } from "@angular/router";
import { AuthorizationGuard } from "../../../guards/authorization.guard";

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: DashboardComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '汇总信息',
      removable: true
    }
  }
];

@NgModule({
  imports: [ CoreModule, SharedModule, RouterModule.forChild(routes) ],
  declarations: [ DashboardComponent ],
  exports: [ RouterModule ]
})
export class DashboardModule {

}
