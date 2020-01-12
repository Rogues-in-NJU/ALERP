import { BaseInfo } from "./base-info";

export interface UserManagementInfoVO extends BaseInfo {

  id?: number;
  name?: string;
  phoneNumber?: string;
  status?: number;

}
