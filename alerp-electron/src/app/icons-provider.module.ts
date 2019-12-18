import {NgModule} from '@angular/core';
import {NZ_ICONS} from 'ng-zorro-antd';

import {
  DashboardOutline, FormOutline, MenuFoldOutline, MenuUnfoldOutline,
  UserOutline, LockOutline, SettingOutline, MailOutline, AppstoreOutline,
  AccountBookOutline, RocketOutline, ShoppingCartOutline, UploadOutline,
  PlusOutline, SaveOutline, LoadingOutline, TransactionOutline
} from '@ant-design/icons-angular/icons';

const icons = [
  MenuFoldOutline,
  MenuUnfoldOutline,
  DashboardOutline,
  FormOutline,
  UserOutline,
  LockOutline,
  SettingOutline,
  MailOutline,
  AppstoreOutline,
  AccountBookOutline,
  RocketOutline,
  ShoppingCartOutline,
  UploadOutline,
  PlusOutline,
  SaveOutline,
  LoadingOutline,
  TransactionOutline
];

@NgModule({
  providers: [
    {provide: NZ_ICONS, useValue: icons}
  ]
})
export class IconsProviderModule {
}
