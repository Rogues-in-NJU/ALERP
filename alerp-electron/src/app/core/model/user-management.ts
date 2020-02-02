import {BaseInfo} from "./base-info";

export interface UserManagementInfoVO extends BaseInfo {

  id?: number;
  name?: string;
  phoneNumber?: string;
  password?: string;
  status?: number;
  cities?:number[]
  authList?: userAuthVO[];
  authList1?: userAuthVO[];
  authList2?: userAuthVO[];
  authList3?: userAuthVO[];
  authList4?: userAuthVO[];
  authList5?: userAuthVO[];
  authList6?: userAuthVO[];
  authList7?: userAuthVO[];
  authList8?: userAuthVO[];
  authList9?: userAuthVO[];
  authList10?: userAuthVO[];
}

export interface userAuthVO {

  id?: number;
  authId?: number;
  description: string;
  action: number;
  userId?: number;
}

