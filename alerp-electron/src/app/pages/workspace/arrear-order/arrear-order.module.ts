import { NgModule } from "@angular/core";
import { CoreModule } from "../../../core/core.module";
import { SharedModule } from "../../../shared/shared.module";
import { RouterModule } from "@angular/router";

@NgModule({
  imports: [
    CoreModule,
    SharedModule,
    RouterModule.forChild([])
  ],
  declarations: [],
  exports: [ RouterModule ]
})
export class ArrearOrderModule {

}
