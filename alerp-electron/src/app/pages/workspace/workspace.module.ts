import { NgModule } from "@angular/core";
import { CoreModule } from "../../core/core.module";
import { SharedModule } from "../../shared/shared.module";
import { WorkspaceComponent } from "./workspace.component";
import { RouteReuseStrategy, RouterModule, Routes } from "@angular/router";
import { SimpleReuseStrategy } from "../../core/strategy/simple-reuse.strategy";
import { TabComponent } from "./tab/tab.component";
import { NzTabsModule } from "ng-zorro-antd";
import { AuthorizationGuard } from "../../guards/authorization.guard";

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  {
    path: 'dashboard',
    loadChildren: './dashboard/dashboard.module#DashboardModule',
    canActivate: [ AuthorizationGuard ],
    data: {
      title:' 页面1',
      isRemove: true
    }
  }, {
    path: 'page1',
    loadChildren: './page1/page1.module#Page1Module',
    canActivate: [ AuthorizationGuard ],
    data: {
      title:' 页面2',
      isRemove: true
    }
  }
];

@NgModule({
  providers: [
    { provide: RouteReuseStrategy, useClass: SimpleReuseStrategy }
  ],
  imports: [ CoreModule, SharedModule, RouterModule.forChild(routes), NzTabsModule ],
  declarations: [ WorkspaceComponent, TabComponent ],
  exports: [ WorkspaceComponent, RouterModule ]
})
export class WorkspaceModule {
}
