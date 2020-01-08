import { BaseInfo } from "./base-info";

export interface ExpenseInfoVO extends BaseInfo {

  title?: string;
  description: string;
  cash: number;
  doneAt: string;

}
