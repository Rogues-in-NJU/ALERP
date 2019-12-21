import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { ProcessingOrderListComponent } from "./processing-order-list/processing-order-list.component";
import { AuthorizationGuard } from "../../../guards/authorization.guard";
import { NzPageHeaderModule } from "ng-zorro-antd";
import { ProcessingOrderInfoComponent } from "./processing-order-info/processing-order-info.component";
import { ProcessingOrderAddComponent } from "./processing-order-add/processing-order-add.component";
import { ProcessingOrderStatusColorPipe, ProcessingOrderStatusPipe } from "./processing-order.pipe";
import { CoreModule } from "../../../core/core.module";
import { SharedModule } from "../../../shared/shared.module";
import { ReactiveFormsModule } from "@angular/forms";

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'list' },
  {
    path: 'list',
    pathMatch: 'full',
    component: ProcessingOrderListComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '加工单列表',
      removable: true
    }
  }, {
    path: 'info/:id',
    component: ProcessingOrderInfoComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '加工单{}',
      removable: true,
      replaceParams: [ 'id' ]
    }
  }, {
    path: 'add',
    pathMatch: 'full',
    component: ProcessingOrderAddComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '新增加工单',
      removable: true,
      replaceParams: [ 'id' ]
    }
  }
];

@NgModule({
  imports: [ CoreModule, SharedModule, RouterModule.forChild(routes), NzPageHeaderModule, ReactiveFormsModule ],
  declarations: [
    ProcessingOrderListComponent,
    ProcessingOrderInfoComponent,
    ProcessingOrderAddComponent,
    ProcessingOrderStatusPipe,
    ProcessingOrderStatusColorPipe
  ],
  exports: [ RouterModule ]
})
export class ProcessingOrderModule {
}
