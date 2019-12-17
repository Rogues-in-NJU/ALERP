import { BaseInfo } from "./base-info";

export interface ProductVO extends BaseInfo {

  id?: number;
  name: string;
  shorthand?: string;
  type: number;
  density: number;
  specification: string;

}
