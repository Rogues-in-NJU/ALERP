import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { UserCenterInfoComponent } from "./user-center-info/user-center-info.component";
import { CoreModule } from "../../../core/core.module";
import { SharedModule } from "../../../shared/shared.module";

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'user' },
  {
    path: 'user',
    component: UserCenterInfoComponent,
    data: {
      title: '用户中心',
      removable: true
    }
  }
];

@NgModule({
  providers: [],
  imports: [ CoreModule, SharedModule, RouterModule.forChild(routes) ],
  declarations: [ UserCenterInfoComponent ],
  exports: [ RouterModule ]
})
export class UserCenterModule {

}
