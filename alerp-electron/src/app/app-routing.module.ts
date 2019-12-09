import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PassportComponent } from "./pages/passport/passport.component";
import { WorkspaceComponent } from "./pages/workspace/workspace.component";
import { LoginComponent } from "./pages/passport/login/login.component";

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'passport' },
  {
    path: 'passport',
    component: PassportComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'login' },
      { path: 'login', component: LoginComponent }
    ]
  },
  {
    path: 'workspace',
    component: WorkspaceComponent,
    loadChildren: './pages/workspace/workspace.module#WorkspaceModule'
  }
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {
}
