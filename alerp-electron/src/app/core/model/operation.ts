import { BaseInfo } from "./base-info";

export interface OperationInfoVO extends BaseInfo {

  id?: number;
  user_name: string;
  created_at: string;
  description: string;

}
