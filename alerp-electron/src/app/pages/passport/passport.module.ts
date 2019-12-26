import { NgModule } from "@angular/core";
import { PassportComponent } from "./passport.component";
import { CoreModule } from "../../core/core.module";
import { SharedModule } from "../../shared/shared.module";
import { LoginComponent } from "./login/login.component";
import { RouterModule } from "@angular/router";
import { ReactiveFormsModule } from "@angular/forms";

@NgModule({
  imports: [ CoreModule, SharedModule, RouterModule, ReactiveFormsModule ],
  declarations: [ PassportComponent, LoginComponent ],
  exports: [ PassportComponent, LoginComponent ]
})
export class PassportModule {
}
