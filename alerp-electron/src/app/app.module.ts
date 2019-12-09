import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { NZ_I18N, zh_CN } from 'ng-zorro-antd';
import { registerLocaleData } from '@angular/common';
import zh from '@angular/common/locales/zh';
import { CoreModule } from "./core/core.module";
import { SharedModule } from "./shared/shared.module";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AppRoutingModule } from "./app-routing.module";
import { PassportModule } from "./pages/passport/passport.module";
import { WorkspaceModule } from "./pages/workspace/workspace.module";

registerLocaleData(zh);

@NgModule({
  imports: [
    CoreModule,
    SharedModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,

    PassportModule,
    WorkspaceModule
  ],
  declarations: [
    AppComponent
  ],
  providers: [ { provide: NZ_I18N, useValue: zh_CN } ],
  bootstrap: [ AppComponent ]
})
export class AppModule {
}
