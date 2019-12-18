import { BaseInfo } from "./base-info";

export interface ExpenseInfoVO extends BaseInfo {

  id?: number;
  title?: string;
  description: string;
  cash: number;
  doneAt: string;

}
