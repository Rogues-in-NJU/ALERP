import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { CustomerListComponent } from "./customer-list/customer-list.component";
import { AuthorizationGuard } from "../../../guards/authorization.guard";
import { CoreModule } from "../../../core/core.module";
import { SharedModule } from "../../../shared/shared.module";
import { CustomerPriceTypePipe, CustomerTypePipe } from "./customer.pipe";
import { ReactiveFormsModule } from "@angular/forms";
import { CustomerInfoComponent } from "./customer-info/customer-info.component";

const routes: Routes = [
  // { path: '', pathMatch: 'full', redirectTo: 'list' },
  {
    path: 'list',
    pathMatch: 'full',
    component: CustomerListComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '客户列表',
      removable: true
    }
  }, {
    path: 'info/:id',
    component: CustomerInfoComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '客户{}',
      removable: true,
      replaceParams: [ 'id' ]
    }
  }
];

@NgModule({
  imports: [ CoreModule, SharedModule, RouterModule.forChild(routes), ReactiveFormsModule ],
  declarations: [ CustomerListComponent, CustomerInfoComponent, CustomerTypePipe, CustomerPriceTypePipe ],
  exports: [ RouterModule ]
})
export class CustomerModule {

}
