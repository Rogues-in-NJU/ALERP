import { BaseInfo } from "./base-info";

export interface PurchaseOrderInfoVO extends BaseInfo {

  id?: number;
  code?: string;
  description: string;
  purchasingCompany: string;
  cash: number;
  salesman?: string;
  doneAt: string;
  status?: number;

  products?: PurchaseOrderProductInfoVO[];

}

export interface PurchaseOrderProductInfoVO {

  id?: number;
  name?: string;
  quantity?: number;
  weight?: number;
  price: number;
  cash: number;

}
