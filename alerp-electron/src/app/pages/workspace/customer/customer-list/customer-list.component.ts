import { Component, OnInit } from "@angular/core";
import { RefreshableTab } from "../../tab/tab.component";
import { CustomerService } from "../../../../core/services/customer.service";
import { Router } from "@angular/router";
import { NzMessageService } from "ng-zorro-antd";
import { TabService } from "../../../../core/services/tab.service";
import { CustomerVO } from "../../../../core/model/customer";
import { ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../../../../core/model/result-vm";
import { HttpErrorResponse } from "@angular/common/http";
import { Objects } from "../../../../core/services/util.service";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";

@Component({
  selector: 'customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: [ './customer-list.component.less' ]
})
export class CustomerListComponent implements RefreshableTab, OnInit {

  isLoading: boolean = false;
  totalPages: number = 1;
  pageIndex: number = 1;
  pageSize: number = 10;

  customerAddVisible: boolean = false;
  customerAddOkLoading: boolean = false;

  customerName: string;
  customerList: CustomerVO[];

  customerAddForm: FormGroup;
  periodFormatter: any = (value: number) => {
    if (Objects.valid(value)) {
      return `${value} 个月`;
    } else {
      return ``;
    }
  };
  payDateFormatter: any = (value: number) => {
    if (Objects.valid(value)) {
      return `${value} 日`;
    } else {
      return ``;
    }
  };

  constructor(
    private router: Router,
    private customer: CustomerService,
    private message: NzMessageService,
    private tab: TabService,
    private fb: FormBuilder
  ) {

  }

  ngOnInit(): void {
    this.customerAddForm = this.fb.group({
      name: [ null, Validators.required ],
      shorthand: [ null ],
      description: [ null ],
      period: [ 1, Validators.required ],
      payDate: [ 20, Validators.required ]
    });
    this.search();
  }

  search(): void {
    console.log(this.customerName);
    const queryParams: TableQueryParams = Object.assign(new TableQueryParams(), {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    });
    this.isLoading = true;
    this.customer.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<CustomerVO>>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== 200) {
          return;
        }
        const tableResult: TableResultVO<CustomerVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.customerList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      }, () => {
        this.isLoading = false;
      });
  }

  refresh(): void {
    this.customerAddOkLoading = false;
    this.customerAddForm.reset({
      name: null,
      shorthand: null,
      description: null,
      period: 1,
      payDate: 20
    });
    this.customerAddVisible = false;
    this.search();
  }

  confirmDelete(id: number): void {
    console.log(id);
  }

  showAddModal(): void {
    this.customerAddVisible = true;
    this.customerAddForm.reset({
      name: null,
      shorthand: null,
      description: null,
      period: 1,
      payDate: 20
    });
    console.log('show modal');
  }

  confirmAdd(): void {
    if (!this.customerAddForm.valid) {
      // 主动触发验证
      Object.values(this.customerAddForm.controls).forEach(item => {
        item.markAsDirty();
        item.updateValueAndValidity();
      });
      return;
    }
    this.customerAddOkLoading = true;
    const customerAddData: CustomerVO = this.customerAddForm.getRawValue();
    this.customer.save(customerAddData)
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          return;
        }
        this.message.success('添加成功!');
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      }, () => {
        this.refresh();
      });
  }

  cancelAdd(): void {
    this.customerAddForm.reset({
      name: null,
      shorthand: null,
      description: null,
      period: 1,
      payDate: 20
    });
    this.customerAddVisible = false;
  }

}
