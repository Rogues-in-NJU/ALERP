import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'list' }
];

@NgModule({
  imports: [],
  declarations: [],
  exports: [RouterModule]
})
export class ProcessingOrderModule {}
