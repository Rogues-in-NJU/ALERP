import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import {ShippingOrderListComponent} from "./shipping-order-list/shipping-order-list.component";
import {ShippingOrderInfoComponent} from "./shipping-order-info/shipping-order-info.component";
import {ShippingOrderAddComponent} from "./shipping-order-add/shipping-order-add.component";
import {AuthorizationGuard} from "../../../guards/authorization.guard";
import {ShippingOrderStatusPipe} from "./shipping-order.pipe";
import {CoreModule} from "../../../core/core.module";
import {SharedModule} from "../../../shared/shared.module";
import {ReactiveFormsModule} from "@angular/forms";

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'list' },
  { path: 'list',
    pathMatch: 'full',
    component: ShippingOrderListComponent,
    canActivate: [AuthorizationGuard],
    data: {
      title: '出货单列表',
      removable: true
    }
  },
  { path: 'add',
    pathMatch: 'full',
    component: ShippingOrderAddComponent,
    canActivate: [AuthorizationGuard],
    data: {
      title: '新增出货单',
      removable: true
    }
  },
  { path: 'info/:id',
    component: ShippingOrderInfoComponent,
    canActivate: [AuthorizationGuard],
    data: {
      title: '出货单{}',
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
  declarations: [ShippingOrderListComponent,
    ShippingOrderInfoComponent,
    ShippingOrderAddComponent,
    ShippingOrderStatusPipe],
  exports: [ RouterModule ]
})
export class ShippingOrderModule {}
