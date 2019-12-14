import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { PurchaseOrderListComponent } from "./purchase-order-list/purchase-order-list.component";
import { AuthorizationGuard } from "../../../guards/authorization.guard";
import { PurchaseOrderAddComponent } from "./purchase-order-add/purchase-order-add.component";

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'list' },
  {
    path: 'list',
    pathMatch: 'full',
    component: PurchaseOrderListComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '采购单列表',
      removable: true
    }
  }, {
    path: 'add',
    pathMatch: 'full',
    component: PurchaseOrderAddComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '新增采购单',
      removable: true
    }
  }
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  declarations: [ PurchaseOrderListComponent, PurchaseOrderAddComponent ],
  exports: [ RouterModule ]
})
export class PurchaseOrderModule {
}
