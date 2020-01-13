import {BaseInfo} from "./base-info";

export interface PasswordInfoVO extends BaseInfo {

  oldPassword: string;
  newPassword: string;

}


