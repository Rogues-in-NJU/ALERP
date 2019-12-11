import { NgModule } from "@angular/core";
import { CoreModule } from "../../../core/core.module";
import { SharedModule } from "../../../shared/shared.module";
import { RouterModule, Routes } from "@angular/router";
import { Page1Component } from "./page1.component";

const routes: Routes = [
  { path: '', pathMatch: 'full', component: Page1Component }
];

@NgModule({
  imports: [ CoreModule, SharedModule, RouterModule.forChild(routes) ],
  declarations: [ Page1Component ],
  exports: [ RouterModule ]
})
export class Page1Module {
}
