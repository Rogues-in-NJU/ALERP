import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import {ProductListComponent} from "./product-list/product-list.component";
import {AuthorizationGuard} from "../../../guards/authorization.guard";
import {SharedModule} from "../../../shared/shared.module";
import {ProductTypePipe} from "./product.pipe";

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'list' },
  { path: 'list',
    pathMatch: 'full',
    component: ProductListComponent,
    canActivate: [ AuthorizationGuard ],
    data: {
      title: '商品管理',
      removable: true
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes), SharedModule],
  declarations: [ProductListComponent,
    ProductTypePipe],
  exports: [ RouterModule ]
})
export class ProductModule {

}
