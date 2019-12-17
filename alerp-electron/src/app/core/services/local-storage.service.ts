import { Injectable } from "@angular/core";

/**
 * 本地存储服务，提供LocalStorage的操作封装，方便保存用户信息等内容。
 * */
@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  readonly localStorage: Storage;

  constructor() {
    if (!localStorage) {
      throw new Error('Current browser does not support Local Storage');
    }
    this.localStorage = localStorage;
  }

  public set(key: string, value: string): void {
    this.localStorage[key] = value;
  }

  public get(key: string): string {
    return this.localStorage[key] || null;
  }

  public setObject(key: string, value: any): void {
    this.localStorage[key] = JSON.stringify(value);
  }

  public getObject<T>(key: string): T {
    return JSON.parse(this.localStorage[key] || '{}');
  }

  public remove(key: string): void {
    this.localStorage.removeItem(key);
  }

}
