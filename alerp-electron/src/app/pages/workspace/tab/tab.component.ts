import { Component } from '@angular/core';
import { SimpleReuseStrategy } from "../../../core/strategy/simple-reuse.strategy";
import { ActivatedRoute, Data, NavigationEnd, Router } from "@angular/router";
import { Title } from "@angular/platform-browser";
import { filter, map, mergeMap } from "rxjs/operators";
import { Observable, UnaryFunction } from "rxjs";
import { CloseTabEvent, CloseTabService } from "../../../core/services/event/close-tab.service";

/**
 * Tab页面组件，注册到workspace module中，提供路由切换时切换tab页面的功能
 * */
@Component({
  selector: 'app-tab',
  templateUrl: './tab.component.html',
  styleUrls: [ './tab.component.less' ]
})
export class TabComponent {

  // 路由列表
  menuList: MenuConfig[] = [];
  // 当前选择的tab index
  currentIndex: number = -1;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private titleService: Title,
    private closeTabService: CloseTabService
  ) {

    // 路由事件
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => this.activatedRoute),
      map(route => {
        while (route.firstChild) {
          route = route.firstChild;
        }
        return route;
      }),
      filter(route => route.outlet === 'primary')
    ).subscribe(
      (route: ActivatedRoute) => {
        const c: MenuConfigConverter = { ...route.snapshot.data };
        // 路由data的标题
        console.log(route);
        const menu: MenuConfig = {...c.converter(route)};
        menu.url = this.router.url;
        const url = menu.url;
        this.titleService.setTitle(menu.title); // 设置网页标题
        const exitMenu = this.menuList.find(info => info.url === url);
        if (!exitMenu) {// 如果不存在那么不添加，
          this.menuList.push(menu);
        }
        this.currentIndex = this.menuList.findIndex(p => p.url === url);
      }
    );

    this.closeTabService.event.subscribe((event: CloseTabEvent) => {
      this.closeUrl(event.url);
    });
  }

  // 关闭选项标签
  closeUrl(url: string): void {
    // 当前关闭的是第几个路由
    const index = this.menuList.findIndex(p => p.url === url);
    // 如果只有一个不可以关闭
    if (this.menuList.length === 1) {
      return;
    }
    this.menuList.splice(index, 1);
    // 删除复用
    // delete SimpleReuseStrategy.handlers[module];
    SimpleReuseStrategy.deleteRouteSnapshot(url);
    // 如果当前删除的对象是当前选中的，那么需要跳转
    if (this.currentIndex === index) {
      // 显示上一个选中
      let menu = this.menuList[ index - 1 ];
      if (!menu) {// 如果上一个没有下一个选中
        menu = this.menuList[ index ];
      }
      // 跳转路由
      this.router.navigate([ menu.url ]);
    }
  }

  /**
   * tab发生改变
   */
  nzSelectChange($event) {
    this.currentIndex = $event.index;
    const menu = this.menuList[ this.currentIndex ];
    // 跳转路由
    this.router.navigate([ menu.url ]);
  }

}

export class MenuConfig {

  url?: string;
  title?: string;
  removable?: boolean;

}

export class MenuConfigConverter {

  converter?: UnaryFunction<ActivatedRoute, MenuConfig> = (route?: ActivatedRoute): MenuConfig => {
    return {};
  }

}
