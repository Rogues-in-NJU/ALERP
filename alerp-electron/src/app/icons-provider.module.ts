import {NgModule} from '@angular/core';
import {NZ_ICONS} from 'ng-zorro-antd';

import {
  DashboardOutline, FormOutline, MenuFoldOutline, MenuUnfoldOutline,
  UserOutline, LockOutline, SettingOutline, MailOutline, AppstoreOutline,
  AccountBookOutline, RocketOutline, ShoppingCartOutline, UploadOutline,
  ContactsOutline, EditOutline, TransactionOutline,
  PlusOutline, SaveOutline, LoadingOutline, PrinterOutline, ShopOutline} from '@ant-design/icons-angular/icons';

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
  ContactsOutline,
  EditOutline,
  TransactionOutline,
  PrinterOutline,
  ShopOutline
];

@NgModule({
  providers: [
    {provide: NZ_ICONS, useValue: icons}
  ]
})
export class IconsProviderModule {
}
