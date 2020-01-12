import { BaseInfo } from "./base-info";

export interface OperationInfoVO extends BaseInfo {

  id?: number;
  userName: string;
  createdAt: string;
  description: string;

}
