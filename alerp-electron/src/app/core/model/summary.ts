export interface SummaryVO {

  processingOrderTotalWeight: number;
  shippingOrderTotalWeight: number;
  shippingOrderTotalCash: number;
  totalReceivedCash: number;
  totalOverdueCash: number;
  purchaseOrderTotalUnpaidCash: number;
  processingOrderTotalNum: number;
  shippingOrderTotalNum: number;

  averagePriceMonthly: number;
  averagePriceCash: number;

}

export interface SummaryProductVO {

  id: number;
  name: string;
  totalWeight: number;
  averagePrice: number;

}
