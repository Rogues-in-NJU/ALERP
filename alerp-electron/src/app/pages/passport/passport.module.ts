import { NgModule } from "@angular/core";
import { PassportComponent } from "./passport.component";
import { CoreModule } from "../../core/core.module";
import { SharedModule } from "../../shared/shared.module";
import { LoginComponent } from "./login/login.component";
import { NzCardModule } from "ng-zorro-antd";
import { RouterModule } from "@angular/router";

@NgModule({
  imports: [ CoreModule, SharedModule, NzCardModule, RouterModule ],
  declarations: [ PassportComponent, LoginComponent ],
  exports: [ PassportComponent, LoginComponent ]
})
export class PassportModule {
}
