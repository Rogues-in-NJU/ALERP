import { Component, OnInit } from "@angular/core";
import { RefreshableTab } from "../../tab/tab.component";
import { CustomerService } from "../../../../core/services/customer.service";
import { Router } from "@angular/router";
import { NzMessageService } from "ng-zorro-antd";
import { RefreshTabEvent, TabService } from "../../../../core/services/tab.service";
import { CustomerVO } from "../../../../core/model/customer";
import { ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../../../../core/model/result-vm";
import { HttpErrorResponse } from "@angular/common/http";
import { Objects, StringUtils } from "../../../../core/services/util.service";
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

  needResetPageIndex: boolean = false;

  constructor(
    private router: Router,
    private customer: CustomerService,
    private message: NzMessageService,
    private tab: TabService,
    private fb: FormBuilder
  ) {

  }

  ngOnInit(): void {
    this.tab.refreshEvent.subscribe((event: RefreshTabEvent) => {
      if (Objects.valid(event) && event.url === '/workspace/customer/list') {
        this.refresh();
      }
    });
    this.customerAddForm = this.fb.group({
      name: [ null, Validators.required ],
      shorthand: [ null ],
      type: [ 2, Validators.required ],
      description: [ null ],
      period: [ 1, Validators.required ],
      payDate: [ 20, Validators.required ]
    });
    this.search();
  }

  search(): void {
    if (this.needResetPageIndex) {
      this.needResetPageIndex = false;
      this.pageIndex = 1;
    }
    const queryParams: TableQueryParams = Object.assign(new TableQueryParams(), {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    });
    if (!StringUtils.isEmpty(this.customerName)) {
      Object.assign(queryParams, {
        name: this.customerName
      });
    }
    this.isLoading = true;
    this.customer.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<CustomerVO>>) => {
        if (!Objects.valid(res)) {
          this.message.error('搜索失败!');
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        const tableResult: TableResultVO<CustomerVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.customerList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
        this.isLoading = false;
      }, () => {
        this.isLoading = false;
      });
  }

  refresh(): void {
    this.customerAddOkLoading = false;
    this.customerAddForm.reset({
      name: null,
      shorthand: null,
      type: 2,
      description: null,
      period: 1,
      payDate: 20
    });
    this.customerAddVisible = false;
    this.search();
  }

  confirmDelete(id: number): void {
    if (!Objects.valid(id)) {
      this.refresh();
      return;
    }
    this.customer.delete(id)
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          this.message.error('删除失败!');
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.message.success('删除成功!');
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
        this.refresh();
      }, () => {
        this.refresh();
      });
  }

  showAddModal(): void {
    this.customerAddVisible = true;
    this.customerAddForm.reset({
      name: null,
      shorthand: null,
      type: 2,
      description: null,
      period: 1,
      payDate: 20
    });
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
        this.message.error('添加失败!');
        if (!Objects.valid(res)) {
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.message.success('添加成功!');
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
        this.refresh();
      }, () => {
        this.refresh();
      });
  }

  cancelAdd(): void {
    this.customerAddForm.reset({
      name: null,
      shorthand: null,
      type: 2,
      description: null,
      period: 1,
      payDate: 20
    });
    this.customerAddVisible = false;
  }

  resetPageIndex(): void {
    // this.pageIndex = 1;
    this.needResetPageIndex = true;
  }

}
