import { NgModule } from "@angular/core";
import { CoreModule } from "../../../core/core.module";
import { SharedModule } from "../../../shared/shared.module";
import { RouterModule, Routes } from "@angular/router";
import { WelcomeComponent } from "./welcome.component";

const routes: Routes = [
  {
    path: '',
    component: WelcomeComponent,
    data: {
      title: '欢迎页面',
      removable: true
    }
  }
];

@NgModule({
  imports: [ CoreModule, SharedModule, RouterModule.forChild(routes) ],
  declarations: [ WelcomeComponent ]
})
export class WelcomeModule {

}
