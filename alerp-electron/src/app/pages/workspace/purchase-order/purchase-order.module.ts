import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { PurchaseOrderListComponent, } from "./purchase-order-list/purchase-order-list.component";
import { AuthorizationGuard } from "../../../guards/authorization.guard";
import { PurchaseOrderAddComponent } from "./purchase-order-add/purchase-order-add.component";
import { CoreModule } from "../../../core/core.module";
import { SharedModule } from "../../../shared/shared.module";
import { PurchaseOrderInfoComponent } from "./purchase-order-info/purchase-order-info.component";
import {
  PurchaseOrderPriceTypePipe,
  PurchaseOrderStatusColorPipe,
  PurchaseOrderStatusPipe
} from "./purchase-order.pipe";
import { ReactiveFormsModule } from "@angular/forms";

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
  }, {
    path: 'info/:id',
    component: PurchaseOrderInfoComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '采购单{}',        // title内容将会被显示在tab的标签上，其中通过{}和replaceParams来依次填充route中的特殊内容
      removable: true,
      replaceParams: [ 'id' ]
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
    PurchaseOrderListComponent,
    PurchaseOrderAddComponent,
    PurchaseOrderInfoComponent,
    PurchaseOrderStatusPipe,
    PurchaseOrderStatusColorPipe,
    PurchaseOrderPriceTypePipe
  ],
  exports: [ RouterModule ]
})
export class PurchaseOrderModule {
}
