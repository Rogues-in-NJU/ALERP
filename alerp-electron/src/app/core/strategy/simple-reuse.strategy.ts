import { ActivatedRouteSnapshot, DetachedRouteHandle, Route, RouteReuseStrategy } from "@angular/router";
import { Objects } from "../services/util.service";

/**
 * 路由复用策略，为了方便客户在不同的tab标签之间切换，避免需要通过刷新整个页面来进入另一个操作页面，
 * 避免工作流的中断。
 * */
export class SimpleReuseStrategy implements RouteReuseStrategy {

  public static handlers: { [ key: string ]: DetachedRouteHandle } = {};
  private static waitDelete: string;

  static reset(): void {
    SimpleReuseStrategy.handlers = {};
    SimpleReuseStrategy.waitDelete = null;
  }

  /**
   * 从缓存中获取快照，若无则返回null
   * */
  retrieve(route: ActivatedRouteSnapshot): DetachedRouteHandle | null {
    if (!route.routeConfig) {
      return null;
    }
    return SimpleReuseStrategy.handlers[ this.getRouteUrl(route) ];
  }

  shouldAttach(route: ActivatedRouteSnapshot): boolean {
    return !!SimpleReuseStrategy.handlers[ this.getRouteUrl(route) ];
  }

  /**
   * 表示对所有路由允许复用 如果你有路由不想利用可以在这加一些业务逻辑判断
   * */
  shouldDetach(route: ActivatedRouteSnapshot): boolean {
    return true;
  }

  /**
   * 进入路由触发，判断是否同一路由
   * */
  shouldReuseRoute(future: ActivatedRouteSnapshot, curr: ActivatedRouteSnapshot): boolean {
    return future.routeConfig === curr.routeConfig
      && JSON.stringify(future.params) === JSON.stringify(curr.params);
  }

  /**
   * 当路由离开时会触发。按path作为key存储路由快照&组件当前实例对象
   * */
  store(route: ActivatedRouteSnapshot, handle: DetachedRouteHandle | null): void {
    if (SimpleReuseStrategy.waitDelete && SimpleReuseStrategy.waitDelete === this.getRouteUrl(route)) {
      // 如果待删除是当前路由则不存储快照
      SimpleReuseStrategy.waitDelete = null;
      return;
    }
    SimpleReuseStrategy.handlers[this.getRouteUrl(route)] = handle;
  }

  private getRouteUrl(route: ActivatedRouteSnapshot): string {
    return route['_routerState'].url.replace(/\//g, '_')
      + '_' + (route.routeConfig.loadChildren || route.routeConfig.component.toString().split('(')[0].split(' ')[1] );
  }

  public static deleteRouteSnapshot(url: string, routeConfig: Route): void {
    const key = url.replace(/\//g, '_')
      + '_' + (routeConfig.loadChildren || routeConfig.component.toString().split('(')[0].split(' ')[1] );
    if (SimpleReuseStrategy.handlers[ key ]) {
      delete SimpleReuseStrategy.handlers[ key ];
    } else {
      SimpleReuseStrategy.waitDelete = key;
    }
  }

}
