import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";

const routes: Routes = [
  { path: '', pathMatch: 'full' }
];

@NgModule({
  imports: [],
  declarations: [],
  exports: [ RouterModule ]
})
export class ShippingOrderModule {}
