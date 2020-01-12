import {BaseInfo} from "./base-info";

export interface UserManagementInfoVO extends BaseInfo {

  id?: number;
  name?: string;
  phoneNumber?: string;
  password?: string;
  status?: number;

  city?:number[]
  authList?: userAuthVO[];
}

export interface userAuthVO {

  id?: 3
  authId?: number;
  description: string;
  action: number;
  userId?: number;
}

