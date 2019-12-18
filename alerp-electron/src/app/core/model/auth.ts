import { BaseInfo } from "./base-info";

export interface AuthVO extends BaseInfo {

  id?: number;
  description: string
  route: string;

}
