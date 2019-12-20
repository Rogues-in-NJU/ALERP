import {BaseInfo} from "./base-info";

export interface ShippingOrderInfoVO extends BaseInfo{
  id?: number
  code?: string
  customerId?: number
  customerName?: string
  arrearOrderId?: string
  state?: number
  cash?: number
  floatingCash?: number
  receivableCash?: number

  products?: ShippingOrderProductInfoVO[];
}

export interface ShippingOrderProductInfoVO extends BaseInfo{
  id?: number
  shippingOrderId?: number
  processingOrderId?: number
  productId?: number
  specification?: string
  quantity?: number
  price?: number
  priceType?: number
  expectedWeight?: number
  weight?: number
  cash?: number
}
