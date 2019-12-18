import { BaseInfo } from "./base-info";

export interface CustomerVO extends BaseInfo {

  id?: number;
  name: string;
  shorthand?: string;
  type: number;
  period: number;
  payDate: number;
  description?: string;

  specialPrices?: CustomerSpecialPriceVO[];

}

export interface CustomerSpecialPriceVO extends BaseInfo {

  id?: number;
  productId: number;
  productName: string;
  price: number;
  unit: number;

}
