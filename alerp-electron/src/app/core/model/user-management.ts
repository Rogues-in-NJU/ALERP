import {BaseInfo} from "./base-info";

export interface UserManagementInfoVO extends BaseInfo {

  id?: number;
  name?: string;
  phoneNumber?: string;
  password?: string;
  status?: number;
  cities?:number[]
  authList?: userAuthVO[];
}

export interface userAuthVO {

  id?: number;
  authId?: number;
  description: string;
  action: number;
  userId?: number;
}

