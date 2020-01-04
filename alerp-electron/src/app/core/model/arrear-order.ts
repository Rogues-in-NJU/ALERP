import { BaseInfo } from "./base-info";

export interface ArrearOrderInfoVO extends BaseInfo {

  id?: number;
  code?: string;
  shippingOrderId?: number;
  shippingOrderCode?: string;
  status?: number;
  invoiceNumber?: string;
  customerId?: number;
  customerName?: string;
  receivableCash?: number;
  receivedCash?: number;
  dueDate?: string;
  overDue?: boolean;

  receipts?: ArrearOrderReceiptRecordVO[];
}

export interface ArrearOrderReceiptRecordVO extends BaseInfo {

  id?: number;
  arrearOrderId?: number;
  status?: number;
  cash?: number;
  salesman?: string;
  description?: string;
  doneAt?: string;

}

export interface ArrearStatisticsVO {

  customers?: CustomerOverduesVO[];
  statistics?: AllOverDuesVO;

}

export interface CustomerOverduesVO {
  customerId?: number;
  customerName?: string;
  total?: number;

  overdues?: OverdueVO[];
}

export interface AllOverDuesVO {
  total?: number;

  overdues?: OverdueVO[];
}

export interface OverdueVO {
  month?: string;
  cash?: number;
}
