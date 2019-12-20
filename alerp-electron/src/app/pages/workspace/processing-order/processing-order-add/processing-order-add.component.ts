import { Component, OnInit } from "@angular/core";

@Component({
  selector: 'processing-order-add',
  templateUrl: './processing-order-add.component.html',
  styleUrls: [ './processing-order-add.component.less' ]
})
export class ProcessingOrderAddComponent implements OnInit {

  isSaving: boolean = false;

  ngOnInit(): void {
  }

}
