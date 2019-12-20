import { BaseInfo } from "./base-info";

export interface PurchaseOrderVO extends BaseInfo {

  id?: number;
  code?: string;
  description: string;
  purchasingCompany: string;
  cash: number;
  salesman?: string;
  doneAt: string;
  status?: number;

  products?: PurchaseOrderProductVO[];

}

export interface PurchaseOrderProductVO {

  id?: number;
  productId: number,
  name?: string;
  quantity?: number;
  weight?: number;
  price: number;
  priceType: number,
  cash: number;

}
