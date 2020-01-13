import {Component} from "@angular/core";
import {FormGroup, FormBuilder} from "@angular/forms";
import {UserManagementInfoVO} from "../../../../core/model/user-management";
import {TabService} from "../../../../core/services/tab.service";
import {ActivatedRoute, Router} from "@angular/router";
import {UserManagementService} from "../../../../core/services/user-management.service";
import {NzMessageService} from "ng-zorro-antd";
import {HttpErrorResponse} from "@angular/common/http";
import {ResultVO} from "../../../../core/model/result-vm";
import {debounceTime} from "rxjs/internal/operators";

@Component({
  selector: 'user-center-info',
  templateUrl: './user-center-info.component.html',
  styleUrls: ['./user-center-info.component.less']
})
export class UserCenterInfoComponent {
  userManagementForm: FormGroup;
  isLoading: boolean = true;
  userManagementData: UserManagementInfoVO;
  isSaving: boolean = false;

  nameTmp: string;
  phoneNumberTmp: string;

  constructor(private closeTabService: TabService,
              private route: ActivatedRoute,
              private router: Router,
              private fb: FormBuilder,
              private userManagement: UserManagementService,
              private message: NzMessageService) {

  }

  ngOnInit(): void {
    this.userManagementForm = this.fb.group({
      name: [null],
      phoneNumber: [null],
    });
    this.userManagement.findSelf()
      .subscribe((res: ResultVO<UserManagementInfoVO>) => {
        console.log(res);
        if (!res) {
          return;
        }
        this.isLoading = false;
        this.userManagementData = res.data;
        this.nameTmp = res.data.name;
        this.phoneNumberTmp = res.data.phoneNumber;
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  saveUser(): void {
    if (!this.userManagementForm.valid) {
      return;
    }
    let formData: any = this.userManagementForm.getRawValue();
    let userManagementAdd: UserManagementInfoVO = this.userManagementData;


    this.isSaving = true;
    this.userManagement.save(userManagementAdd)
      .pipe(debounceTime(3000))
      .subscribe((res: ResultVO<any>) => {
        console.log(res);
        this.message.success('修改成功!');
        this.isSaving = false;
        // TODO: 跳转回列表页面
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

  tabClose(): void {
    this.closeTabService.closeEvent.emit({
      url: this.router.url,
      refreshUrl: null
    });
  }
}
