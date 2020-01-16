import {Component, OnInit, ViewChild, ElementRef} from "@angular/core";
import {ClosableTab, RefreshableTab} from "../../tab/tab.component";
import {ShippingOrderInfoVO, ShippingOrderProductInfoVO} from "../../../../core/model/shipping-order";
import {TabService} from "../../../../core/services/tab.service";
import {ActivatedRoute, Router} from "@angular/router";
import {NzMessageService} from "ng-zorro-antd";
import {ShippingOrderService} from "../../../../core/services/shipping-order.service";
import {ProductVO} from "../../../../core/model/product";
import {BehaviorSubject, Observable} from "rxjs";
import {QueryParams, ResultCode, ResultVO} from "../../../../core/model/result-vm";
import {debounceTime, map, switchMap} from "rxjs/operators";
import {ProcessingOrderProductVO, ProcessingOrderVO} from "../../../../core/model/processing-order";
import {Objects, SpecificationUtils, StringUtils} from "../../../../core/services/util.service";
import {HttpErrorResponse} from "@angular/common/http";
import {ProductService} from "../../../../core/services/product.service";
import {ENgxPrintComponent} from "e-ngx-print";
import {FormGroup, FormBuilder} from "@angular/forms";

@Component({
  selector: 'shipping-order-info',
  templateUrl: './shipping-order-info.component.html',
  styleUrls: ['./shipping-order-info.component.less']
})
export class ShippingOrderInfoComponent implements ClosableTab, OnInit {

  isLoading: boolean = true;
  shippingOrderCode: string;
  shippingOrderId: number;
  shippingOrderData: ShippingOrderInfoVO = {};
  processingOrderInfoProductCountIndex: number = 0;

  printCSS: string[];

  printStyle: string;

  printForm: FormGroup;
  isVisibleWeight = false;
  isVisibleCount = false;
  hasTax: string;
  remarks: string;

  constructor(private closeTabService: TabService,
              private route: ActivatedRoute,
              private router: Router,
              private shippingOrder: ShippingOrderService,
              private product: ProductService,
              private fb: FormBuilder,
              private message: NzMessageService,
              private elRef: ElementRef) {
    this.shippingOrderCode = this.route.snapshot.params['code'];

    this.printCSS = ['http://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css'];

    this.printStyle =
      `
        th, td {
            color: blue !important;
        }
        `;
  }


  @ViewChild('print1', {static: false})
  printComponent: ENgxPrintComponent;
  showPrint: boolean = false;

  printComplete() {
    // console.log('打印完成！');
    this.showPrint = false;
  }

  handleCancel(): void {
    this.isVisibleWeight = false;
    this.isVisibleCount = false;
  }

  showModal(): void {
    if (this.shippingOrderData.products[0].priceType == 2) {
      this.isVisibleCount = true;
    } else {
      this.isVisibleWeight = true;
    }
  }

  customPrint(print: string) {
    this.isVisibleWeight = false;
    this.isVisibleCount = false;

    if (!this.printForm.valid) {
      return;
    }
    let formData: any = this.printForm.getRawValue();
    // let userManagementAdd: UserManagementInfoVO = this.userManagementData;
    this.hasTax = formData.hasTax;
    this.remarks = formData.remarks;

    this.showPrint = true;
    if (this.shippingOrderData.products[0].priceType == 2) {
      const printHTML: any = this.elRef.nativeElement.childNodes[4];
      // console.log(printHTML);
      this.printComponent.print(printHTML);
    } else {
      const printHTML: any = this.elRef.nativeElement.childNodes[5];
      // console.log(printHTML);
      this.printComponent.print(printHTML);
    }
  }

  stringMoney: string;

  moneyToString(num: number) {
    const digits = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
    const radices = ['', '拾', '佰', '仟', '万', '亿'];
    const bigRadices = ['', '万', '亿'];
    const decimals = ['角', '分']; // 这里只精确到分
    const cn_dollar = '元';
    const cn_integer = '整';
    // int_str = Math.floor(num).toString();
    // float_str = num % 1;
    const num_arr = num.toString().split('.');
    const int_str = num_arr[0] || '';
    let float_str = num_arr[1] || '';
    if (float_str.length > 2) {
      float_str = float_str.substr(0, 2);
    }
    let outputCharacters = '';
    if (int_str) {
      let zeroCount = 0;
      const int_len = int_str.length;
      for (var i = 0; i < int_len; i++) {
        const p = int_len - i - 1;
        const d = int_str.substr(i, 1);
        const quotient = p / 4;
        const modulus = p % 4;
        if (d === '0') {
          zeroCount++;
        } else {
          if (zeroCount > 0) {
            outputCharacters += digits[0];
          }
          zeroCount = 0;
          outputCharacters += digits[d] + radices[modulus];
        }
        if (modulus === 0 && zeroCount < 4) {
          outputCharacters += bigRadices[quotient];
          zeroCount = 0;
        }
      }
      outputCharacters += cn_dollar;
    }

    if (float_str) {
      const float_len = float_str.length;
      for (let i = 0; i < float_len; i++) {
        const d = float_str.substr(i, 1);
        if (d !== '0') {
          outputCharacters += digits[d] + decimals[i];
        }
      }
    }

    if (outputCharacters === '') {
      outputCharacters = digits[0] + cn_dollar;
    }

    if (float_str) {
      outputCharacters += cn_integer;
    }
    return outputCharacters;
  }

  ngOnInit(): void {
    this.printForm = this.fb.group({
      hasTax: [null],
      remarks: [null],
    });
    this.shippingOrderId = this.route.snapshot.params['id'];
    this.reload();
  }

  tabClose(): void {
    this.closeTabService.closeEvent.emit({
      url: this.router.url,
      refreshUrl: null,
      routeConfig: this.route.snapshot.routeConfig
    });
  }

  reload(): void {
    // console.log(this.shippingOrderId);
    this.shippingOrder.find(this.shippingOrderId)
      .subscribe((res: ResultVO<ShippingOrderInfoVO>) => {
        console.log(res);
        if (!Objects.valid(res)) {
          return;
        }
        this.isLoading = false;
        this.shippingOrderData = res.data;
        this.stringMoney = this.moneyToString(this.shippingOrderData.cash);
        // console.log(this.stringMoney);
        if (Objects.valid(this.shippingOrderData.products)) {
          this.shippingOrderData.products.forEach(item => {
            item['_id'] = this.processingOrderInfoProductCountIndex++;
          })
        }
      }, (error: HttpErrorResponse) => {
        this.message.error(error.message);
      });
  }

}
