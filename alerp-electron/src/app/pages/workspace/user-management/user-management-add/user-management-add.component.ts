import {Component, OnInit} from "@angular/core";
import {ClosableTab} from "../../tab/tab.component";
import {TabService} from "../../../../core/services/tab.service";
import {Router} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ResultVO} from "../../../../core/model/result-vm";
import {debounceTime} from "rxjs/operators";
import {NzMessageService} from "ng-zorro-antd";
import {HttpErrorResponse} from "@angular/common/http";
import {UserManagementService} from "../../../../core/services/user-management.service";
import {UserManagementInfoVO, userAuthVO} from "../../../../core/model/user-management";

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
  cityTmp: number[];

  authsTmp: userAuthVO[] = [
    <userAuthVO>{description: "查看商品列表", action: 0},
    <userAuthVO>{description: "新增或者编辑商品", action: 0},
    <userAuthVO>{description: "查看商品详情", action: 0},
    <userAuthVO>{description: "废弃商品", action: 0}];

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
              private fb: FormBuilder,
              private message: NzMessageService) {

  }

  ngOnInit(): void {
    this.userManagementForm = this.fb.group({
      name: [null, Validators.required],
      phoneNumber: [null, Validators.required],
      password: [null, Validators.required],
    });

  }

  saveUser(): void {
    if (!this.userManagementForm.valid) {
      return;
    }
    let formData: any = this.userManagementForm.getRawValue();
    let userManagementAdd: UserManagementInfoVO = {
      name: formData.name,
      phoneNumber: formData.phoneNumber,
      password: formData.password,
      city: this.cityTmp,
      authList: this.authsTmp
    };
    // private Integer id;
    // private String updateAt;
    // private List<Auth> authList;
    console.log(userManagementAdd);
    this.isSaving = true;
    this.userManagement.save(userManagementAdd)
      .pipe(debounceTime(3000))
      .subscribe((res: ResultVO<any>) => {
        console.log(res);
        this.message.success('添加成功!');
        this.isSaving = false;
        // TODO: 跳转回列表页面
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  tabClose(): void {
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
