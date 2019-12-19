import {NgModule} from "@angular/core";
import {CoreModule} from "../../core/core.module";
import {SharedModule} from "../../shared/shared.module";
import {WorkspaceComponent} from "./workspace.component";
import {RouteReuseStrategy, RouterModule, Routes} from "@angular/router";
import {SimpleReuseStrategy} from "../../core/strategy/simple-reuse.strategy";
import {TabComponent} from "./tab/tab.component";

const routes: Routes = [
  {path: '', pathMatch: 'full', redirectTo: 'dashboard'},
  {
    path: 'dashboard',
    loadChildren: './dashboard/dashboard.module#DashboardModule'
  }, {
    path: 'customer',
    loadChildren: './customer/customer.module#CustomerModule'
  }, {
    path: 'purchase-order',
    loadChildren: './purchase-order/purchase-order.module#PurchaseOrderModule'
  }, {
    path: 'expense',
    loadChildren: './expense/expense.module#ExpenseModule'
  }, {
    path: 'user-management',
    loadChildren: './user-management/user-management.module#UserManagementModule'
  }, {
    path: 'user-center',
    loadChildren: './user-center/user-center.module#UserCenterModule'
  }, {
    path: 'product',
    loadChildren: './product/product.module#ProductModule'
  }, {
    path: 'shipping-order',
    loadChildren: './shipping-order/shipping-order.module#ShippingOrderModule'
  }
];

@NgModule({
  providers: [
    {provide: RouteReuseStrategy, useClass: SimpleReuseStrategy}
  ],
  imports: [CoreModule, SharedModule, RouterModule.forChild(routes)],
  declarations: [WorkspaceComponent, TabComponent],
  exports: [WorkspaceComponent, RouterModule]
})
export class WorkspaceModule {
}
