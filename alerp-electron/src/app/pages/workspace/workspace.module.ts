import { NgModule, OnInit } from "@angular/core";
import { CoreModule } from "../../core/core.module";
import { SharedModule } from "../../shared/shared.module";
import { WorkspaceComponent } from "./workspace.component";
import { Router, RouterModule, Routes } from "@angular/router";
import { AuthorizationGuard } from "../../guards/authorization.guard";
import { AuthService, UserService } from "../../core/services/user/user.service";
import { LocalStorageService } from "../../core/services/local-storage/local-storage.service";

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'dashboard', loadChildren: './dashboard/dashboard.module#DashboardModule', canActivate: [ AuthorizationGuard ] }
];

@NgModule({
  imports: [ CoreModule, SharedModule, RouterModule.forChild(routes) ],
  declarations: [ WorkspaceComponent ],
  exports: [ WorkspaceComponent, RouterModule ]
})
export class WorkspaceModule {
}
