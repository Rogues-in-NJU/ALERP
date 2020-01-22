import {Component, OnInit} from "@angular/core";
import {ClosableTab} from "../../tab/tab.component";
import {TabService} from "../../../../core/services/tab.service";
import {Router, ActivatedRoute} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ResultVO, TableResultVO, ResultCode} from "../../../../core/model/result-vm";
import {debounceTime} from "rxjs/operators";
import {NzMessageService} from "ng-zorro-antd";
import {HttpErrorResponse} from "@angular/common/http";
import {UserManagementService} from "../../../../core/services/user-management.service";
import {UserManagementInfoVO, userAuthVO} from "../../../../core/model/user-management";
import {AuthVO} from "../../../../core/model/auth";
import {Objects} from "../../../../core/services/util.service";

@Component({
  selector: 'user-management-add',
  templateUrl: './user-management-add.component.html',
  styleUrls: ['./user-management-add.component.less']
})
export class UserManagementAddComponent implements ClosableTab, OnInit {

  userManagementForm: FormGroup;
  editCache: {_id?: number, data?: TempPurchaseOrderProductInfoVO, product?: TempProductVO, isAdd?: boolean} = {};
  isLoading: boolean = false;

  isSaving: boolean = false;
  cityTmp: number[] = [];

  authsTmp: userAuthVO[] = [
    <userAuthVO>{id: 0, authId: 1, description: "查看商品列表", action: 1},
    <userAuthVO>{id: 1, authId: 2, description: "新增或者编辑商品", action: 1},
    <userAuthVO>{id: 2, authId: 3, description: "查看商品详情", action: 1},
    <userAuthVO>{id: 3, authId: 4, description: "废弃商品", action: 0},
    <userAuthVO>{id: 4, authId: 9, description: "获取收款单详情", action: 1},
    <userAuthVO>{id: 5, authId: 10, description: "修改收款单截止日期", action: 1},
    <userAuthVO>{id: 6, authId: 11, description: "修改发票流水号", action: 1},
    <userAuthVO>{id: 7, authId: 12, description: "查询收款单列表", action: 1},
    <userAuthVO>{id: 8, authId: 13, description: "获取欠款统计列表", action: 0},
    <userAuthVO>{id: 9, authId: 14, description: "查看客户详细信息", action: 1},
    <userAuthVO>{id: 10, authId: 15, description: "删除客户", action: 0},
    <userAuthVO>{id: 11, authId: 16, description: "获取客户列表", action: 1},
    <userAuthVO>{id: 12, authId: 17, description: "新增客户/修改客户信息", action: 1},
    <userAuthVO>{id: 13, authId: 18, description: "新增公司支出", action: 1},
    <userAuthVO>{id: 14, authId: 19, description: "删除公司支出", action: 0},
    <userAuthVO>{id: 15, authId: 20, description: "获取公司支出列表", action: 1},
    <userAuthVO>{id: 16, authId: 21, description: "获取加工单列表", action: 1},
    <userAuthVO>{id: 17, authId: 22, description: "查询加工单详情", action: 1},
    <userAuthVO>{id: 18, authId: 23, description: "新增或者更新加工单", action: 1},
    <userAuthVO>{id: 19, authId: 24, description: "新增或者更新加工单商品", action: 1},
    <userAuthVO>{id: 20, authId: 25, description: "删除加工单商品", action: 1},
    <userAuthVO>{id: 21, authId: 26, description: "打印加工单", action: 1},
    <userAuthVO>{id: 22, authId: 27, description: "废弃加工单", action: 0},
    <userAuthVO>{id: 23, authId: 28, description: "获取采购单列表", action: 0},
    <userAuthVO>{id: 24, authId: 29, description: "获取采购单详情", action: 0},
    <userAuthVO>{id: 25, authId: 30, description: "新增采购单", action: 0},
    <userAuthVO>{id: 26, authId: 31, description: "新增采购单付款记录", action: 0},
    <userAuthVO>{id: 27, authId: 32, description: "废弃采购单付款记录", action: 0},
    <userAuthVO>{id: 28, authId: 33, description: "废弃采购单", action: 0},
    <userAuthVO>{id: 29, authId: 34, description: "增加收款记录", action: 1},
    <userAuthVO>{id: 30, authId: 35, description: "删除收款记录", action: 0},
    <userAuthVO>{id: 31, authId: 36, description: "删除出货单", action: 0},
    <userAuthVO>{id: 32, authId: 37, description: "获取出货单列表", action: 1},
    <userAuthVO>{id: 33, authId: 38, description: "新增出货单", action: 1},
    <userAuthVO>{id: 34, authId: 39, description: "查看出货单详情", action: 1},
    <userAuthVO>{id: 35, authId: 40, description: "获取商品平均单价列表", action: 0},
    <userAuthVO>{id: 36, authId: 41, description: "获取汇总信息", action: 0},
    <userAuthVO>{id: 37, authId: 42, description: "获取商品汇总信息", action: 0},
    <userAuthVO>{id: 38, authId: 43, description: "获取供货商列表", action: 0},
    <userAuthVO>{id: 39, authId: 44, description: "新增或者更新供货商", action: 0},
    <userAuthVO>{id: 40, authId: 45, description: "删除供货商", action: 0},
    <userAuthVO>{id: 41, authId: 46, description: "删除用户", action: 0},
    <userAuthVO>{id: 42, authId: 47, description: "获取用户列表", action: 0},
    <userAuthVO>{id: 43, authId: 48, description: "获取用户详细信息", action: 0},
    <userAuthVO>{id: 44, authId: 49, description: "修改当前用户密码", action: 1},
    <userAuthVO>{id: 45, authId: 50, description: "获取登录用户详细信息", action: 1},
    <userAuthVO>{id: 46, authId: 51, description: "用户操作日志查询", action: 0},
    <userAuthVO>{id: 47, authId: 52, description: "新增用户/修改用户信息", action: 0},
    <userAuthVO>{id: 48, authId: 53, description: "查询权限资源列表", action: 0},
    <userAuthVO>{id: 49, authId: 54, description: "编辑用户权限", action: 0},
    <userAuthVO>{id: 50, authId: 55, description: "查询用户的拥有权限", action: 0}];

  saveCityTmp(value: string[]): void {
    if (value.length === 1) {
      if (value[0] == "1") {
        this.cityTmp = [1];
      } else {
        this.cityTmp = [2];
      }
    }
    if (value.length === 2) {
      this.cityTmp = [1, 2];
    }
  }

  constructor(private closeTab: TabService,
              private userManagement: UserManagementService,
              private router: Router,
              private route: ActivatedRoute,
              private tab: TabService,
              private fb: FormBuilder,
              private message: NzMessageService) {

  }

  ngOnInit(): void {
    this.userManagement.getAuthList()
      .subscribe((res: ResultVO<TableResultVO<AuthVO>>) => {
        if (!Objects.valid(res)) {
          this.message.error("请求失败！");
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      });
    this.userManagementForm = this.fb.group({
      name: [null, Validators.required],
      phoneNumber: [null, Validators.required],
    });
  }

  saveUser(): void {
    if (this.cityTmp.length == 0) {
      this.message.error('请至少选择一个城市!');
      return;
    }
    if (!this.userManagementForm.valid) {
      return;
    }
    let formData: any = this.userManagementForm.getRawValue();
    let userManagementAdd: UserManagementInfoVO = {
      name: formData.name,
      phoneNumber: formData.phoneNumber,
      cities: this.cityTmp,
      authList: this.authsTmp
    };
    this.isSaving = true;
    this.userManagement.save(userManagementAdd)
      .subscribe((res: ResultVO<any>) => {
        console.log(res);
        if (!Objects.valid(res)) {
          this.message.error('新增失败!');
          return;
        }
        if (res.code !== ResultCode.SUCCESS.code) {
          this.message.error(res.message);
          return;
        }
        this.message.success('新增成功!');
        this.isSaving = false;
        this.tabClose();
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      });
  }

  tabClose(): void {
    this.tab.closeEvent.emit({
      url: this.router.url,
      goToUrl: '/workspace/user-management/list',
      refreshUrl: '/workspace/user-management/list',
      routeConfig: this.route.snapshot.routeConfig
    });
  }

  confirmAdd(id: number): void {
    this.authsTmp[id].action = 1;
  }

  confirmAbandon(id: number): void {
    this.authsTmp[id].action = 0;
  }
}

interface TempProductVO {

  id: number,
  name: string
  [key: string]: any;

}

interface TempPurchaseOrderProductInfoVO {

  id?: number;
  name?: string;
  quantity?: number;
  weight?: number;
  price?: number;
  cash?: number;

}
