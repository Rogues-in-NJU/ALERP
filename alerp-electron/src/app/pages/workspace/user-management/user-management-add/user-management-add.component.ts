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

  //商品
  authsTmp1: userAuthVO[] = [
    <userAuthVO>{id: 0, authId: 1, description: "查看商品列表", action: 1},
    <userAuthVO>{id: 1, authId: 2, description: "新增或者编辑商品", action: 1},
    <userAuthVO>{id: 2, authId: 3, description: "查看商品详情", action: 1},
    <userAuthVO>{id: 3, authId: 4, description: "废弃商品", action: 0},
  ];
  //欠款明细
  authsTmp2: userAuthVO[] = [
    <userAuthVO>{id: 0, authId: 9, description: "获取欠款明细详情", action: 1},
    <userAuthVO>{id: 1, authId: 10, description: "修改欠款明细截止日期", action: 1},
    <userAuthVO>{id: 2, authId: 11, description: "修改发票流水号", action: 1},
    <userAuthVO>{id: 3, authId: 12, description: "查询欠款明细列表", action: 1},
    <userAuthVO>{id: 4, authId: 13, description: "获取欠款统计", action: 0},
    <userAuthVO>{id: 5, authId: 34, description: "增加收款记录", action: 1},
    <userAuthVO>{id: 6, authId: 35, description: "删除收款记录", action: 0},
  ];
  //客户
  authsTmp3: userAuthVO[] = [
    <userAuthVO>{id: 0, authId: 14, description: "查看客户详细信息", action: 1},
    <userAuthVO>{id: 1, authId: 15, description: "删除客户", action: 0},
    <userAuthVO>{id: 2, authId: 16, description: "获取客户列表", action: 1},
    <userAuthVO>{id: 3, authId: 17, description: "新增客户/修改客户信息", action: 1},
  ];
  //支出
  authsTmp4: userAuthVO[] = [
    <userAuthVO>{id: 0, authId: 18, description: "新增公司支出", action: 1},
    <userAuthVO>{id: 1, authId: 19, description: "删除公司支出", action: 0},
    <userAuthVO>{id: 2, authId: 20, description: "获取公司支出列表", action: 1},
  ];
  //加工单
  authsTmp5: userAuthVO[] = [
    <userAuthVO>{id: 0, authId: 21, description: "获取加工单列表", action: 1},
    <userAuthVO>{id: 1, authId: 22, description: "查询加工单详情", action: 1},
    <userAuthVO>{id: 2, authId: 23, description: "新增或者更新加工单", action: 1},
    <userAuthVO>{id: 3, authId: 24, description: "新增或者更新加工单商品", action: 1},
    <userAuthVO>{id: 4, authId: 25, description: "删除加工单商品", action: 1},
    <userAuthVO>{id: 5, authId: 26, description: "打印加工单", action: 1},
    <userAuthVO>{id: 6, authId: 27, description: "废弃加工单", action: 0},
  ];
  //采购单
  authsTmp6: userAuthVO[] = [
    <userAuthVO>{id: 0, authId: 28, description: "获取采购单列表", action: 0},
    <userAuthVO>{id: 1, authId: 29, description: "获取采购单详情", action: 0},
    <userAuthVO>{id: 2, authId: 30, description: "新增采购单", action: 0},
    <userAuthVO>{id: 3, authId: 31, description: "新增采购单付款记录", action: 0},
    <userAuthVO>{id: 4, authId: 32, description: "废弃采购单付款记录", action: 0},
    <userAuthVO>{id: 5, authId: 33, description: "废弃采购单", action: 0},
  ];
  //出货单
  authsTmp7: userAuthVO[] = [
    <userAuthVO>{id: 0, authId: 36, description: "删除出货单", action: 0},
    <userAuthVO>{id: 1, authId: 37, description: "获取出货单列表", action: 1},
    <userAuthVO>{id: 2, authId: 38, description: "新增出货单", action: 1},
    <userAuthVO>{id: 3, authId: 39, description: "查看出货单详情", action: 1},
  ];
  //汇总信息
  authsTmp8: userAuthVO[] = [
    <userAuthVO>{id: 0, authId: 40, description: "获取商品平均单价列表", action: 0},
    <userAuthVO>{id: 1, authId: 41, description: "获取汇总信息", action: 0},
    <userAuthVO>{id: 2, authId: 42, description: "获取商品汇总信息", action: 0},
  ];
  //供货商
  authsTmp9: userAuthVO[] = [
    <userAuthVO>{id: 0, authId: 43, description: "获取供货商列表", action: 0},
    <userAuthVO>{id: 1, authId: 44, description: "新增或者更新供货商", action: 0},
    <userAuthVO>{id: 2, authId: 45, description: "删除供货商", action: 0},
  ];
  //用户相关
  authsTmp10: userAuthVO[] = [
    <userAuthVO>{id: 0, authId: 46, description: "删除用户", action: 0},
    <userAuthVO>{id: 1, authId: 47, description: "获取用户列表", action: 0},
    <userAuthVO>{id: 2, authId: 48, description: "获取用户详细信息", action: 0},
    <userAuthVO>{id: 3, authId: 49, description: "修改当前用户密码", action: 1},
    <userAuthVO>{id: 4, authId: 50, description: "获取登录用户详细信息", action: 1},
    <userAuthVO>{id: 5, authId: 51, description: "用户操作日志查询", action: 0},
    <userAuthVO>{id: 6, authId: 52, description: "新增用户/修改用户信息", action: 0},
    <userAuthVO>{id: 7, authId: 53, description: "查询权限资源列表", action: 0},
    <userAuthVO>{id: 8, authId: 54, description: "编辑用户权限", action: 0},
    <userAuthVO>{id: 9, authId: 55, description: "查询用户的拥有权限", action: 0},
  ];

  authsTmp: userAuthVO[] = [];

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
        this.message.error(error.message);
      });
    this.userManagementForm = this.fb.group({
      name: [null, Validators.required],
      phoneNumber: [null, Validators.required],
    });
  }

  saveUser(): void {
    for(const user of this.authsTmp1){
      this.authsTmp.push(user);
    }
    for(const user of this.authsTmp2){
      this.authsTmp.push(user);
    }
    for(const user of this.authsTmp3){
      this.authsTmp.push(user);
    }
    for(const user of this.authsTmp4){
      this.authsTmp.push(user);
    }
    for(const user of this.authsTmp5){
      this.authsTmp.push(user);
    }
    for(const user of this.authsTmp6){
      this.authsTmp.push(user);
    }
    for(const user of this.authsTmp7){
      this.authsTmp.push(user);
    }
    for(const user of this.authsTmp8){
      this.authsTmp.push(user);
    }
    for(const user of this.authsTmp9){
      this.authsTmp.push(user);
    }
    for(const user of this.authsTmp10){
      this.authsTmp.push(user);
    }

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
      authList: this.authsTmp,
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
        this.message.error(error.message);
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

  confirmAdd1(id: number): void {
    this.authsTmp1[id].action = 1;
  }
  confirmAdd2(id: number): void {
    this.authsTmp2[id].action = 1;
  }
  confirmAdd3(id: number): void {
    this.authsTmp3[id].action = 1;
  }
  confirmAdd4(id: number): void {
    this.authsTmp4[id].action = 1;
  }
  confirmAdd5(id: number): void {
    this.authsTmp5[id].action = 1;
  }
  confirmAdd6(id: number): void {
    this.authsTmp6[id].action = 1;
  }
  confirmAdd7(id: number): void {
    this.authsTmp7[id].action = 1;
  }
  confirmAdd8(id: number): void {
    this.authsTmp8[id].action = 1;
  }
  confirmAdd9(id: number): void {
    this.authsTmp9[id].action = 1;
  }
  confirmAdd10(id: number): void {
    this.authsTmp10[id].action = 1;
  }

  confirmAbandon1(id: number): void {
    this.authsTmp1[id].action = 0;
  }
  confirmAbandon2(id: number): void {
    this.authsTmp2[id].action = 0;
  }
  confirmAbandon3(id: number): void {
    this.authsTmp3[id].action = 0;
  }
  confirmAbandon4(id: number): void {
    this.authsTmp4[id].action = 0;
  }
  confirmAbandon5(id: number): void {
    this.authsTmp5[id].action = 0;
  }
  confirmAbandon6(id: number): void {
    this.authsTmp6[id].action = 0;
  }
  confirmAbandon7(id: number): void {
    this.authsTmp7[id].action = 0;
  }
  confirmAbandon8(id: number): void {
    this.authsTmp8[id].action = 0;
  }
  confirmAbandon9(id: number): void {
    this.authsTmp9[id].action = 0;
  }
  confirmAbandon10(id: number): void {
    this.authsTmp10[id].action = 0;
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
