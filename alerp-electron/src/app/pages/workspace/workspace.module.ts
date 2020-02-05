import { NgModule } from "@angular/core";
import { CoreModule } from "../../core/core.module";
import { SharedModule } from "../../shared/shared.module";
import { CityPipe, WorkspaceComponent } from "./workspace.component";
import { RouteReuseStrategy, RouterModule, Routes } from "@angular/router";
import { SimpleReuseStrategy } from "../../core/strategy/simple-reuse.strategy";
import { TabComponent } from "./tab/tab.component";
import { CookieModule } from "ngx-cookie";

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'welcome' },
  {
    path: 'welcome',
    loadChildren: './welcome/welcome.module#WelcomeModule'
  }, {
    path: 'dashboard',
    loadChildren: './dashboard/dashboard.module#DashboardModule'
  }, {
    path: 'customer',
    loadChildren: './customer/customer.module#CustomerModule'
  }, {
    path: 'supplier',
    loadChildren: './supplier/supplier.module#SupplierModule'
  }, {
    path: 'purchase-order',
    loadChildren: './purchase-order/purchase-order.module#PurchaseOrderModule'
  }, {
    path: 'processing-order',
    loadChildren: './processing-order/processing-order.module#ProcessingOrderModule'
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
  }, {
    path: 'arrear-order',
    loadChildren: './arrear-order/arrear-order.module#ArrearOrderModule'
  }, {
    path: 'reconciliation',
    loadChildren: './reconciliation/reconciliation.module#ReconciliationModule'
  }
];

@NgModule({
  providers: [
    { provide: RouteReuseStrategy, useClass: SimpleReuseStrategy }
  ],
  imports: [ CoreModule, SharedModule, RouterModule.forChild(routes), CookieModule.forRoot() ],
  declarations: [ WorkspaceComponent, TabComponent, CityPipe ],
  exports: [ WorkspaceComponent, RouterModule ]
})
export class WorkspaceModule {
}
