import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { CoreModule } from "../../../core/core.module";
import { SharedModule } from "../../../shared/shared.module";
import { AuthorizationGuard } from "../../../guards/authorization.guard";
import { SupplierListComponent } from "./supplier-list/supplier-list.component";
import { ReactiveFormsModule } from "@angular/forms";

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'list' },
  {
    path: 'list',
    pathMatch: 'full',
    component: SupplierListComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '供应商列表',
      removable: true
    }
  }
];

@NgModule({
  imports: [ CoreModule, SharedModule, RouterModule.forChild(routes), ReactiveFormsModule ],
  declarations: [ SupplierListComponent ],
  exports: [ RouterModule ]
})
export class SupplierModule {

}
