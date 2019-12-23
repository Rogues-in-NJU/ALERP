import {Component, OnInit} from "@angular/core";
import {ClosableTab} from "../../tab/tab.component";
import {TabService} from "../../../../core/services/tab.service";
import {Router} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ResultVO} from "../../../../core/model/result-vm";
import {BehaviorSubject, Observable} from "rxjs";
import {debounceTime, map, switchMap} from "rxjs/operators";
import {NzMessageService} from "ng-zorro-antd";
import {DateUtils, Objects} from "../../../../core/services/util.service";
import {HttpErrorResponse} from "@angular/common/http";
import {UserManagementService} from "../../../../core/services/user-management.service";
import {AuthVO} from "../../../../core/model/auth";
import {AuthService} from "../../../../core/services/user.service";
import {UserManagementInfoVO} from "../../../../core/model/user-management";

@Component({
  selector: 'user-management-add',
  templateUrl: './user-management-add.component.html',
  styleUrls: ['./user-management-add.component.less']
})
export class UserManagementAddComponent implements ClosableTab, OnInit {

  userManagementForm: FormGroup;
  editCache: {_id?: number, data?: TempPurchaseOrderProductInfoVO, product?: TempProductVO, isAdd?: boolean} = {};
  userCountIndex: number = 0;
  searchAuths: AuthVO[];
  searchChange$: BehaviorSubject<string> = new BehaviorSubject('');
  isLoading: boolean = false;

  isSaving: boolean = false;
  radioValue1 = '0';
  radioValue2 = '0';

  disabledDate: any = (current: Date): boolean => {
    return DateUtils.compare(current, new Date()) > 0;
  };

  constructor(private closeTab: TabService,
              private userManagement: UserManagementService,
              private router: Router,
              private fb: FormBuilder,
              private auth: AuthService,
              private message: NzMessageService) {

  }

  ngOnInit(): void {
    this.userManagementForm = this.fb.group({
      name: [ null, Validators.required ],
      phone_number: [ null, Validators.required ],
      radioValue1: [ 0, Validators.required ],
      radioValue2: [ null, Validators.required ]
    });

  }

  saveUser(): void {
    if (!this.userManagementForm.valid) {
      return;
    }
    let formData: any = this.userManagementForm.getRawValue();
    let userManagementAdd: UserManagementInfoVO = {
      name: formData.name,
      phone_number: formData.phone_number,
      // radioValue1: formData.radioValue1,
      // radioValue2: formData.radioValue2
    };

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
