import {Component, OnInit} from "@angular/core";
import {ClosableTab} from "../../tab/tab.component";
import {TabService} from "../../../../core/services/tab.service";
import {Router, ActivatedRoute} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ResultVO} from "../../../../core/model/result-vm";
import {BehaviorSubject, Observable} from "rxjs";
import {debounceTime, map, switchMap} from "rxjs/operators";
import {NzMessageService} from "ng-zorro-antd";
import {DateUtils, Objects} from "../../../../core/services/util.service";
import {HttpErrorResponse} from "@angular/common/http";
import {ExpenseInfoVO} from "../../../../core/model/expense";
import {ExpenseService} from "../../../../core/services/expense.service";

@Component({
  selector: 'expense-add',
  templateUrl: './expense-add.component.html',
  styleUrls: ['./expense-add.component.less']
})
export class ExpenseAddComponent implements ClosableTab, OnInit {

  expenseForm: FormGroup;
  isLoading: boolean = false;

  isSaving: boolean = false;

  disabledDate: any = (current: Date): boolean => {
    return DateUtils.compare(current, new Date()) > 0;
  };

  constructor(private closeTab: TabService,
              private expense: ExpenseService,
              private route: ActivatedRoute,
              private router: Router,
              private fb: FormBuilder,
              private message: NzMessageService,
              private tab: TabService) {

  }

  ngOnInit(): void {
    this.expenseForm = this.fb.group({
      title: [null, Validators.required],
      description: [null, Validators.required],
      cash: [null, Validators.required],
      doneAt: [null, Validators.required]
    });

  }

  saveExpense(): void {
    if (!this.expenseForm.valid) {
      return;
    }
    let formData: any = this.expenseForm.getRawValue();
    let expenseAdd: ExpenseInfoVO = {
      title: formData.title,
      description: formData.description,
      cash: formData.cash,
      doneAt: DateUtils.format(formData.doneAt),
    };
    // console.log(formData);
    this.isSaving = true;
    this.expense.save(expenseAdd)
      .pipe(debounceTime(3000))
      .subscribe((res: ResultVO<any>) => {
        // console.log(res);
        this.message.success('添加成功!');
        this.isSaving = false;
        this.tabClose();
      }, (error: HttpErrorResponse) => {
        this.message.error('网络异常，请检查网络或者尝试重新登录!');
      }, () => {
        this.tab.closeEvent.emit({
          url: this.router.url,
          goToUrl: '/workspace/expense/list',
          refreshUrl: '/workspace/expense/list',
          routeConfig: this.route.snapshot.routeConfig
        });
      });
  }

  tabClose(): void {
    this.tab.closeEvent.emit({
      url: this.router.url,
      goToUrl: '/workspace/expense/list',
      refreshUrl: '/workspace/expense/list',
      routeConfig: this.route.snapshot.routeConfig
    });
  }

}
