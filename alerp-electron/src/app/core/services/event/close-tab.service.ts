import { EventEmitter, Injectable, OnInit } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class CloseTabService implements OnInit{

  readonly _event: EventEmitter<CloseTabEvent>;

  constructor() {
    this._event = new EventEmitter<CloseTabEvent>();
  }

  ngOnInit(): void {

  }

  get event(): EventEmitter<CloseTabEvent> {
    return this._event;
  }

}

export interface CloseTabEvent {

  readonly url: string;
  readonly refreshUrl: string | null;

}
