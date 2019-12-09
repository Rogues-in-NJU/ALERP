import { NgModule } from "@angular/core";
import { DashboardComponent } from "./dashboard.component";
import { CoreModule } from "../../../core/core.module";
import { SharedModule } from "../../../shared/shared.module";
import { RouterModule, Routes } from "@angular/router";

const routes: Routes = [
  { path: '', pathMatch: 'full', component: DashboardComponent }
];

@NgModule({
  imports: [ CoreModule, SharedModule, RouterModule.forChild(routes) ],
  declarations: [ DashboardComponent ],
  exports: [ RouterModule ]
})
export class DashboardModule {
}
