import { Component, OnInit } from "@angular/core";
import { SupplierVO } from "../../../../core/model/supplier";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { SupplierService } from "../../../../core/services/supplier.service";
import { NzMessageService } from "ng-zorro-antd";
import { ResultCode, ResultVO, TableQueryParams, TableResultVO } from "../../../../core/model/result-vm";
import { Objects, StringUtils } from "../../../../core/services/util.service";
import { HttpErrorResponse } from "@angular/common/http";
import { RefreshableTab } from "../../tab/tab.component";

@Component({
  selector: 'supplier-list',
  templateUrl: './supplier-list.component.html',
  styleUrls: [ './supplier-list.component.less' ]
})
export class SupplierListComponent implements RefreshableTab, OnInit {

  isLoading: boolean = false;
  totalPages: number = 1;
  pageIndex: number = 1;
  pageSize: number = 10;

  supplierAddVisible: boolean = false;
  supplierAddOkLoading: boolean = false;

  supplierEditVisible: boolean = false;
  supplierEditOkLoading: boolean = false;

  supplierName: string;
  supplierList: SupplierVO[];

  supplierAddForm: FormGroup;
  supplierEditForm: FormGroup;

  shouldRefreshIndex: boolean = false;

  constructor(
    private router: Router,
    private supplier: SupplierService,
    private fb: FormBuilder,
    private message: NzMessageService
  ) {

  }

  ngOnInit(): void {
    this.supplierAddForm = this.fb.group({
      name: [ null, Validators.required ],
      description: [ null ]
    });
    this.supplierEditForm = this.fb.group({
      id: [ null, Validators.required ],
      name: [ null, Validators.required ],
      description: [ null ],
      updatedAt: [ null ],
      updatedById: [ null ]
    });
    this.search();
  }

  search(): void {
    if (this.shouldRefreshIndex) {
      this.pageIndex = 1;
      this.shouldRefreshIndex = false;
    }
    const queryParams: TableQueryParams = Object.assign(new TableQueryParams(), {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize
    });
    if (!StringUtils.isEmpty(this.supplierName)) {
      Object.assign(queryParams, {
        name: this.supplierName
      });
    }
    this.isLoading = true;
    this.supplier.findAll(queryParams)
      .subscribe((res: ResultVO<TableResultVO<SupplierVO>>) => {
        if (!Objects.valid(res)) {
          this.message.error('搜索失败!');
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        const tableResult: TableResultVO<SupplierVO> = res.data;
        this.totalPages = tableResult.totalPages;
        this.pageIndex = tableResult.pageIndex;
        this.pageSize = tableResult.pageSize;
        this.supplierList = tableResult.result;
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
        this.isLoading = false;
      }, () => {
        this.isLoading = false;
      });
  }

  refresh(): void {
    this.supplierAddOkLoading = false;
    this.supplierAddForm.reset({
      name: null,
      description: null
    });
    this.supplierAddVisible = false;
    this.supplierEditOkLoading = false;
    this.supplierEditForm.reset({
      id: null,
      name: null,
      description: null,
      updatedAt: null,
      updatedById: null
    });
    this.supplierEditVisible = false;
    this.search();
  }

  confirmDelete(id: number): void {
    this.supplier.delete(id)
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
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
        this.refresh();
      }, () => {
        this.refresh();
      });
  }

  showAddModal(): void {
    this.supplierAddVisible = true;
    this.supplierAddForm.reset({
      name: null,
      description: null
    });
  }

  confirmAdd(): void {
    if (!this.supplierAddForm.valid) {
      Object.values(this.supplierAddForm.controls).forEach(item => {
        item.markAsDirty();
        item.updateValueAndValidity();
      });
      return;
    }
    this.supplierAddOkLoading = true;
    const supplierAddData: SupplierVO = this.supplierAddForm.getRawValue();
    this.supplier.save(supplierAddData)
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          this.message.error('添加失败!');
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.message.success('添加成功!');
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
        this.refresh();
      }, () => {
        this.refresh();
      });
  }

  cancelAdd(): void {
    this.supplierAddForm.reset({
      name: null,
      description: null
    });
    this.supplierAddVisible = false;
  }

  showEditModal(id: number): void {
    const index: number = this.supplierList.findIndex(item => item.id === id);
    if (index === -1) {
      return;
    }
    this.supplierEditVisible = true;
    this.supplierEditForm.reset(this.supplierList[ index ]);
  }

  confirmEdit(): void {
    if (!this.supplierEditForm.valid) {
      Object.values(this.supplierEditForm.controls).forEach(item => {
        item.markAsDirty();
        item.updateValueAndValidity();
      });
      return;
    }
    this.supplierEditOkLoading = true;
    const supplierEditData: SupplierVO = this.supplierEditForm.getRawValue();
    this.supplier.save(supplierEditData)
      .subscribe((res: ResultVO<any>) => {
        if (!Objects.valid(res)) {
          this.message.error('修改失败!');
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.message.success('修改成功!');
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
        this.refresh();
      }, () => {
        this.refresh();
      });
  }

  refreshPageIndex(): void {
    this.shouldRefreshIndex = true;
  }

  cancelEdit(): void {
    this.supplierEditForm.reset({
      id: null,
      name: null,
      description: null
    });
    this.supplierEditVisible = false;
  }

}
