import { EventEmitter, Injectable } from "@angular/core";
import { Route } from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class TabService {

  readonly _closeEvent: EventEmitter<CloseTabEvent>;
  readonly _refreshEvent: EventEmitter<RefreshTabEvent>;

  constructor() {
    this._closeEvent = new EventEmitter<CloseTabEvent>();
    this._refreshEvent = new EventEmitter<RefreshTabEvent>();
  }

  get closeEvent(): EventEmitter<CloseTabEvent> {
    return this._closeEvent;
  }

  get refreshEvent(): EventEmitter<RefreshTabEvent> {
    return this._refreshEvent;
  }

}

export interface CloseTabEvent {

  readonly url: string;
  readonly refreshUrl?: string | null;
  readonly goToUrl?: string | null;
  readonly routeConfig?: Route;

}

export interface RefreshTabEvent {

  readonly url: string;

}
