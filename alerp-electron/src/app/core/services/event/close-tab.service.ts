import { EventEmitter, Injectable, OnInit } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class CloseTabService {

  readonly _event: EventEmitter<CloseTabEvent>;

  constructor() {
    this._event = new EventEmitter<CloseTabEvent>();
  }

  get event(): EventEmitter<CloseTabEvent> {
    return this._event;
  }

}

export interface CloseTabEvent {

  readonly url: string;
  readonly refreshUrl?: string | null;
  readonly goToUrl?: string | null;

}
