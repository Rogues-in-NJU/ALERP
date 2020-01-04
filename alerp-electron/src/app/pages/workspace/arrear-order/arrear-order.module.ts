import { NgModule } from "@angular/core";
import { CoreModule } from "../../../core/core.module";
import { SharedModule } from "../../../shared/shared.module";
import {RouterModule, Routes} from "@angular/router";
import {AuthorizationGuard} from "../../../guards/authorization.guard";
import {ArrearOrderListComponent} from "./arrear-order-list/arrear-order-list.component";
import {ArrearOrderInfoComponent} from "./arrear-order-info/arrear-order-info.component";
import {ArrearOrderStatusColorPipe, ArrearOrderStatusPipe} from "./arrear-order.pipe";
import {ArrearOrderStatisticsComponent} from "./arrear-order-statistics/arrear-order-statistics.component";


const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'list' },
  {
    path: 'list',
    pathMatch: 'full',
    component: ArrearOrderListComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '欠款明细列表',
      removable: true
    }
  }, {
    path: 'info/:code',
    component: ArrearOrderInfoComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '欠款明细{}',        // title内容将会被显示在tab的标签上，其中通过{}和replaceParams来依次填充route中的特殊内容
      removable: true,
      replaceParams: [ 'code' ]
    }
  },{
    path: 'statistics',
    component: ArrearOrderStatisticsComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '欠款统计',        // title内容将会被显示在tab的标签上，其中通过{}和replaceParams来依次填充route中的特殊内容
      removable: true,
    }
  }
];


@NgModule({
  imports: [
    CoreModule,
    SharedModule,
    RouterModule.forChild(routes)
  ],
  declarations: [ArrearOrderListComponent,
    ArrearOrderInfoComponent,
    ArrearOrderStatusPipe,
    ArrearOrderStatusColorPipe,
    ArrearOrderStatisticsComponent],
  exports: [ RouterModule ]
})
export class ArrearOrderModule {

}
