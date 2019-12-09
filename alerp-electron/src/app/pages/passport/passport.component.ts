import { Component, OnInit } from "@angular/core";
import { NzMessageService } from "ng-zorro-antd";

@Component({
  selector: 'app-passport',
  templateUrl: './passport.component.html',
  styleUrls: [ './passport.component.less' ]
})
export class PassportComponent implements OnInit {

  constructor(
    private message: NzMessageService
  ) {
  }

  ngOnInit(): void {
  }

}
