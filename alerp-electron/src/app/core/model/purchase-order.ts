import { BaseInfo } from "./base-info";

export interface PurchaseOrderVO extends BaseInfo {

  id?: number;
  code?: string;
  description: string;
  supplierId: number;
  supplierName?: string;
  cash: number;
  salesman?: string;
  doneAt: string;
  status?: number;

  products?: PurchaseOrderProductVO[];

  paymentRecords?: PurchaseOrderPaymentRecordVO[];

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

export interface PurchaseOrderPaymentRecordVO extends BaseInfo {

  id?: number,
  purchaseOrderId: number,
  cash: number,
  status?: number,
  description?: string,
  salesman?: string,
  doneAt: string

}
