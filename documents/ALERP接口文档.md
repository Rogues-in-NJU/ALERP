```
返回值包装格式

{
  "code": number,
  "message": string,
  "data": {
    ...   
  }
}

下面的response的都是data里面的值，返回的时候请用以上模板包装。
```
### 商品模块
#### 获取商品列表
```
GET
/api/product/list
params:

* pageIndex(从1开始)
* pageSize
* name (name / shorthand)
* type

res:
{
  totalPages: number,
  pageIndex: number,
  pageSize: number,
  result: [{
    id: string,
    name: string,
    shorthand: string,
    type: number,
    density: number,
    specification: string,
    createdAt: string,
    createdById: string,
    createdByName: string
  }]
}
```

#### 查看商品详情
```
GET
/api/product/:id

res:
{
  id: string,
  name: string,
  shorthand: string,
  type: number,
  density: number,
  specification: string,
  createdAt: string,
  createdById: string,
  createdByName: string
}
```

#### 新增/修改商品
```
POST
/api/product

body:
{
  id: string,
  name: string,
  shorthand: string,
  type: number,
  density: number,
  specification: string
}

res:
{}
```

#### 删除商品
```
GET
/api/product/delete/:id


res:
{}
```

### 采购单模块

#### 获取采购单列表
```
GET
/api/purchase-order/list

params:

* pageIndex(从1开始)
* pageSize
* id
* status
* doneStartTime
* doneEndTime

res:
{
  totalPages: number,
  pageIndex: number,
  pageSize: number,
  result: [{
    id: number,
    code: string,
    description: string,
    supplierId: number,
    supplierName: string,
    cash: number,
    salesman: string,
    status: number (0 1 2 3...),
    doneAt: string (yyyy-MM-dd HH:mm),
    createdAt: string,
    createdById: number,
    createdByName: string
  }]
}

```

#### 通过采购单id获取详细信息
```
GET
/api/purchase-order/:id

res:
{
  id: number,
  code: number,
  description: string,
  supplierId: number,
  supplierName: string,
  cash: number,
  salesman: string,
  status: number (0 1 2 3...),
  doneAt: string (yyyy-MM-dd HH:mm),
  createdAt: string,
  createdById: number,
  createdByName: string,
  
  products: [{
    id: number,
    productId: number,
    name: string,
    quantity: number,
    weight: number,
    price: number,
    cash: number
  }],

  //12.24 新增
  paymentRecords:[{
    id: number,
    purchaseOrderId:number,
    cash:number,
    status,number,
    description:string,
    salesman:string,
    doneAt:string,
    createdById: number,
    createdByName: string,
    deletedById: number,
    deletedByName: string,
    }]
}
```

#### 新增采购单
```
POST
/api/purchase-order

body:

{
  description: string,
  supplierId: number,
  cash: number,
  salesman: string,
  doneAt: string (yyyy-MM-dd HH:mm)
  
  products: [{
    id: number,
    productId: number,
    quantity: number,
    weight: number,
    price: number,
    priceType: number, // 1是元/千克, 2是元/件
    cash: number
  }]
}

res:
{}
```

#### 新增采购单付款记录
```
POST
/api/purchase-order/payment-record

body:

{
    purchaseOrderId: number,
    cash:number
    description: string,
    salesman: string,
    doneAt: string
}

res:
{}
```

#### 废弃采购单付款记录
```
GET
/api/purchase-order/payment-record/delete/:id


res:
{}
```


#### 废弃采购单
```
GET
/api/purchase-order/abandon/:id

res:
{}
```

### 公司支出

#### 查看公司支出列表
```
GET
/api/expense/list

params:

* pageIndex(从1开始)
* pageSize

res:
{
  totalPages: number,
  pageIndex: number,
  pageSize: number,
  result: [{
    id: number,
    title: string,
    description: string,
    cash: number,
    doneAt: string,
    createdAt: string,
    createdById: string,
    createdByName: string
  }]
}
```

#### 新增公司支出
```
POST
/api/expense

body:
{
  title: string,
  description: string,
  cash: number,
  doneAt: string
}

res:
{}
```

#### 删除公司支出
```
GET
/api/expense/delete/:id

res:
{}
```

### 用户管理

#### 查看用户列表

```
GET
/api/user/list

params:
* pageSize
* pageIndex
* userid
* name
* phoneNumber
* status

res:
{
  totalPages: number,
  pageIndex: number,
  pageSize: number,
  result: [{
    id: number,
    name: string,
    phoneNumber: string,
    // 不要传password回来
    status: number
  }]
}
```

#### 通过用户id获取详细信息

```
GET
/api/user/:id

res:
{
  id: number | null,
  name: string,
  phoneNumber: string(在职的员工手机号唯一),
  password: string
  
  auth: [{
    id: string,
    action: number(0不可读 1只读 2可读写)
  }]
}
```

#### 新增用户/修改用户信息

```
POST
/api/user

body:
{
  id: number | null,
  name: string,
  phoneNumber: string(在职的员工手机号唯一),
  password: string
  
  auth: [{
    id: string,
    action: number(0不可读 1只读 2可读写)
  }]
}

res:
{}
```

#### 删除用户
```
GET
/api/user/delete/:id

res:
{}
```

#### 查看用户操作记录
```
GET
/api/operation-log/list

params:
* pageIndex
* pageSize
* userName
* opeartionStartTime
* operationEndTime

res:
{
}
```

### 客户模块
#### 查看客户列表
```
GET
/api/customer/list

params:
* pageSize
* pageIndex
* name (可以搜name可以搜shorthand)

res:
{
  totalPages: number,
  pageIndex: number,
  pageSize: number,
  result: [{
    id: number,
    name: string,
    shorthand: string,
    type: number,
    isAway: number,    // 1.6新增。1表示客户跑了；0表示没跑。 客户跑了：距离上次购买超过25天。
    period: number,
    payDate: number,
    description: string,
    createdAt: string,
    createdById: string,
    createdByName: string
  }]
}
```
#### 查看客户详情
```
GET
/api/customer/:id

res:
{
  id: number,
  name: string,
  shorthand: string,
  type: number,
  period: number,
  payDate: number,
  description: string,
  createdAt: string,
  createdById: string,
  createdByName: string,
  
  specialPrices: [{
    id: number,
    productId: string,
    productName: string,
    price: number,
    createdAt: string,
    createdById: string,
    createdByName: string
  }]
}
```


#### 新增客户/编辑客户
```
POST
/api/customer

body:
{
  id: number,
  name: string,
  shorthand: string,
  type: number,
  period: number,
  payDate: number,
  description: string
  
  specialPrices: [{
    id: number,
    productId: string,
    price: number
  }]
}

res: 
{}
```

#### 删除客户
```
GET
/api/customer/delete/:id

res:
{}
```


### 加工单模块
#### 查看加工单列表
```
GET
/api/processing-order/list

params:
* pageSize
* pageIndex
* id                   //实际上搜索code
* customerName (name 和 shorthand)
* status
* createAtStartTime
* createAtEndTime

res:
{
  totalPages: number,
  pageIndex: number,
  pageSize: number,
  result: [{
    id: number,
    code: string,
    customerId: number,
    customerName: string,
    shippingOrderId: number,
    salesman: string,
    status: number,
    createdAt: string,
    createdById: string,
    createdByName: string
  }]
}
```

#### 加工单详情
```
GET
/api/processing-order/:id

res:
{
  id: number,
  code: string,
  customerId: number,
  customerName: string,
  shippingOrderId: number,
  salesman: string,
  status: number,
  createdAt: string,
  createdById: string,
  createdByName: string,
  updatedAt: string
  
  products: [{
    id: number,
    processingOrderId: number,
    productId: number,
    productName: string,
    type: number,
    density: number,
    productSpecification: string (原材料规格),
    specification: string (客户定做的商品规格),
    quantity: number,
    expectedWeight: number

    //1.10 新增
    isEditable: boolean, //如果该商品属于客户的特价商品，为false；默认为true
    specialPrice: number, // 特价
    specialPriceType: number, //特价里的计价方式 ；按件计算/按重量计算
  }]
  
  totalWeight: number(新增，打印时需要)
}
```

#### 新增加工单
```
POST
/api/processing-order

body:
{
  customerId: number,
  salesman: string,
  products: [{
    productId: number,
    specification: string (客户定做的商品规格),
    quantity: number,
    expectedWeight: number
  }]
}

res:
{}
```

#### 新增/修改加工单的商品关联
```
POST
/api/processing-order/product

body:
{
  id: number,
  processingOrderId: number,
  productId: number,
  specification: string (客户定做的商品规格),
  quantity: number,
  expectedWeight: number

  processingOrderUpdatedAt: String        //1.6新增，加工单的更新时间，用于控制版本  

}

res:
{}
```

#### 删除加工单的商品管理
```
GET
/api/processing-order/product/delete/:id

res:
{}
```

#### 打印加工单
```
GET
/api/processing-order/print/:id

res:
{}
```

#### 废弃加工单
```
GET
/api/processing-order/abandon/:id

res:
{}
```

### 出货单管理
#### 查看出货单列表
```
GET
/api/shipping-order/list

params:
* pageSize
* pageIndex
* id                   //实际上搜索code
* customerName (name 和 shorthand)
* status
* createAtStartTime
* createAtEndTime

res:
{
  totalPages: number,
  pageIndex: number,
  pageSize: number,
  result: [{
    id: number,
    code: string,
    customerId: number,
    customerName: string,
    arrearOrderId: number,
    cash: number,
    floatingCash: number,
    receivableCash: number,
    status: number,
    createdAt: string,
    createdById: string,
    createdByName: string
  }]
}
```


#### 查看出货单详情
```
GET
/api/shipping-order/:id

res:
{
  id: number,
  code: string,
  customerId: number,
  customerName: string,
  arrearOrderId: number,
  cash: number,
  floatingCash: number,
  receivableCash: number,
  totalWeight: number,     //1.6 新增 。本单汇总的重量，用于打印
  status: number,
  createdAt: string,
  createdById: string,
  createdByName: string,
   
  processingOrderIdsCodes: [   //1.10 新增，解决 code和id 的关联问题
    {
      processingOrderId: number,
      processingOrderCode: string,
    }
  ],

  products: [{
    id: number,
    processingOrderId: number,
    processingOrderCode: string,            // 12.28 新增
    productId: number,
    productName: string,
    type: number,
    specification: string (客户定做的商品规格),
    quantity: number,
    expectedWeight: number,
    price: number,
    priceType: number                      //12.28 新增
    weight: number,
    cash: number
  }];
}
```

#### 新增出货单
```
POST
/api/shipping-order

body:
{
  customerId: number,
  cash: number,
  floatingCash: number,
  receivableCash: number
  
  products: [{
    processingOrderId: number,
    productId: number,
    specification: string (客户定做的商品规格),
    quantity: number,
    expectedWeight: number,
    price: number,
    weight: number,
    cash: number
  }];
}

res:
{
  shippingOrderId: number,
  arrearOrderId: number
}
```

#### 打印出货单
```
GET
/api/shipping-order/print/:id

res:
{}
```

#### 废弃出货单
```
GET
/api/shipping-order/abandon/:id

res:
{}
```

### 欠款明细模块

#### 获取欠款明细列表
```
GET
/api/arrear-order/list

params:
* pageSize
* pageIndex
* id                   //实际上搜索code
* customerName (name 和 shorthand)
* invoiceNumber
* shippingOrderId
* status
* createAtStartTime
* createAtEndTime

res:
{
  totalPages: number,
  pageIndex: number,
  pageSize: number,
  result: [{
    id: number,
    code: string,
    customerId: number,
    customerName: string,
    shippingOrderId： number;            //12.24新增
    shippingOrderCode: string;           //12.24新增
    receivableCash: number,
    receivedCash: number,
    dueDate: string,
    overDue: boolean (是否逾期),
    status: number,
    createdAt: string,
    createdById: string,
    createdByName: string
  }]
}
```

#### 欠款明细详情
```
GET
/api/arrear-order/:id

res:
{
  id: number,
  code: string,
  customerId: number,
  customerName: string,
  shippingOrderId： number;            //12.24新增
  shippingOrderCode: string;           //12.24新增
  receivableCash: number,
  receivedCash: number,
  dueDate: string,
  overDue: boolean (是否逾期),
  status: number,
  createdAt: string,
  createdById: string,
  createdByName: string,
  
  receiptRecords: [{
    id: number,
    status: number,
    cash: number,
    salesman: string,
    description: string,
    doneAt: string (升序),
    createdAt: string,
    createdById: string,
    createdByName: string
  }]
}
```

#### 修改欠款明细时间
```
POST
/api/arrear-order/due-date

body:
{
  id: number,
  dueDate: string
}

res:
{}
```

#### 修改发票流水号  // 1.3 新增
```
POST
/api/arrear-order/invoice-number

body:
{
  id: number,
  invoiceNumber: string
}

res:
{}
```

#### 添加收款记录
```
POST
/api/arrear-order/receipt-record

body:
{
  arrearOrderId: number,
  cash: number,
  salesman: string,
  description: string,
  doneAt: string
}

res:
{}
```

#### 废弃收款记录
```
GET
/api/arrear-order/receipt-record/:id

res:
{}
```

### 欠款统计模块

#### 获取欠款统计列表
```
GET

/api/overdue-warning

res:
{
  customers:[{
    customerId: number,
    customerName: string,
    overdues: [{
      month: string,
      cash: number,
    }]
    total: number,
  }],
  statistics:{
    overdues: [{
      month: string,
      cash: number
    }],
    total:number
  },
}
```

### 汇总信息模块

### 供货商模块                        //12.24新增
#### 查看供货商列表
```
GET
/api/supplier/list

params:
* pageSize
* pageIndex
* name 

res:
{
  totalPages: number,
  pageIndex: number,
  pageSize: number,
  result: [{
    id: number,
    name: string,
    description: string,
    createdAt: string,
    createdById: string,
    createdByName: string
  }]
}
```

#### 新增供货商/编辑供货商
```
POST
/api/supplier

body:
{
  id: number,
  name: string,
  description: string

}

res: 
{}
```

#### 删除供货商
```
GET
/api/supplier/delete/:id

res:
{}
```

### 汇总信息模块
#### 汇总信息
```
GET
/api/summary/info

res:
{
  processingOrderTotalWeight: number, // 加工单铝材总重量
  shippingOrderTotalWeight: number,  // 出货单铝材总重量
  shippingOrderTotalCash: number,  // 新增出货单金额
  totalReceivedCash: number,  // 收款金额
  totalOverdueCash: number,  // 逾期金额
  purchaseOrderTotalUnpaidCash: number,  // 采购单未付款金额
  processingOrderTotalNum: number,  // 生成加工单数
  shippingOrderTotalNum: number,  // 生成出货单数

  averagePriceMonthly: number,  // 现金客户平均单价
  averagePriceCash: number  // 月结客户平均单价
}
```

#### 汇总商品平均单价
```
GET
/api/summary/product

params:
* pageIndex
* pageSize
* name

res:
{
  totalPages: number,
  pageIndex: number,
  pageSize: number,
  result: [{
    id: number,
    name: string,
    totalWeight: number,
    averagePrice: number
  }]
}

```