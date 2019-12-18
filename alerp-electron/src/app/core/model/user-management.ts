import { BaseInfo } from "./base-info";

export interface UserManagementInfoVO extends BaseInfo {

  id?: number;
  name?: string;
  phone_number?: string;
  status?: number;

}
