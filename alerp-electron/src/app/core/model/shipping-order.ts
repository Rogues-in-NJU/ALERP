import {BaseInfo} from "./base-info";

export interface ShippingOrderInfoVO extends BaseInfo{
  id?: number
  code?: string
  customerId?: number
  customerName?: string
  arrearOrderId?: string
  arrearOrderCode?: string
  status?: number
  cash?: number
  floatingCash?: number
  receivableCash?: number

  products?: ShippingOrderProductInfoVO[];
  processingOrderCodes?: string[]
}

export interface ShippingOrderProductInfoVO extends BaseInfo{
  id?: number
  shippingOrderId?: number
  shippingOrderCode?: string
  processingOrderId?: number
  processingOrderCode?: string
  productId?: number
  productName?: string
  type?: number,
  density?: number
  specification?: string
  quantity?: number
  price?: number
  priceType?: number
  expectedWeight?: number
  weight?: number
  cash?: number
}
