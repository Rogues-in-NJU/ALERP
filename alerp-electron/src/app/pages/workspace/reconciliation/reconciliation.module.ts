import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {AuthorizationGuard} from "../../../guards/authorization.guard";
import {CoreModule} from "../../../core/core.module";
import {SharedModule} from "../../../shared/shared.module";
import {ReactiveFormsModule} from "@angular/forms";
import {ReconciliationUserListComponent} from "./reconciliation-user-list/reconciliation-user-list.component";
import {ReconciliationShippingOrderListComponent} from "./reconciliation-shipping-order-list/reconciliation-shipping-order-list.component";

const routes: Routes = [
  {path: '', pathMatch: 'full', redirectTo: 'user-list'},
  {
    path: 'user-list',
    pathMatch: 'full',
    component: ReconciliationUserListComponent,
    canActivate: [AuthorizationGuard],
    data: {
      title: '月结客户列表',
      removable: true
    }
  },{
    path: 'shipping-order-list/:id',
    component: ReconciliationShippingOrderListComponent,
    canActivate: [AuthorizationGuard],
    data: {
      title: '客户{}的出货单',        // title内容将会被显示在tab的标签上，其中通过{}和replaceParams来依次填充route中的特殊内容
      removable: true,
      replaceParams: ['id']
    }
  }
];

@NgModule({
  imports: [
    CoreModule,
    SharedModule,
    RouterModule.forChild(routes),
    ReactiveFormsModule
  ],
  declarations: [
    ReconciliationUserListComponent,
    ReconciliationShippingOrderListComponent
  ],
  exports: [RouterModule]
})
export class ReconciliationModule {
}
