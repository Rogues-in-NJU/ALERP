import { Component } from '@angular/core';
import { SimpleReuseStrategy } from "../../../core/strategy/simple-reuse.strategy";
import { ActivatedRoute, NavigationEnd, Route, Router } from "@angular/router";
import { Title } from "@angular/platform-browser";
import { filter, map } from "rxjs/operators";
import { CloseTabEvent, TabService } from "../../../core/services/tab.service";
import { Objects } from "../../../core/services/util.service";

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
    private tab: TabService
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
        // 路由data的标题
        const menu: MenuConfig = { ...route.snapshot.data };
        menu.url = this.router.url;
        menu.routeConfig = route.routeConfig;
        if (menu.replaceParams) {
          for (const s of menu.replaceParams) {
            menu.title = menu.title.replace('{}', route.snapshot.params[ s ])
          }
        }
        const url = menu.url;
        this.titleService.setTitle(menu.title); // 设置网页标题
        const exitMenu = this.menuList.find(info => info.url === url);
        if (!exitMenu) {// 如果不存在那么不添加，
          this.menuList.push(menu);
        }
        this.currentIndex = this.menuList.findIndex(p => p.url === url);
      }
    );

    this.tab.closeEvent.subscribe((event: CloseTabEvent) => {
      this.closeUrl(event);
    });
  }

  // 关闭选项标签
  closeUrl(event: CloseTabEvent): void {
    const url: string = event.url;
    const goToUrl: string = event.goToUrl;
    const refreshUrl: string = event.refreshUrl;
    // 当前关闭的是第几个路由
    const index = this.menuList.findIndex(p => p.url === url);
    this.menuList.splice(index, 1);
    // 删除复用
    SimpleReuseStrategy.deleteRouteSnapshot(url, event.routeConfig);

    // 如果menu空了跳转路由（默认路由/给定路由）
    if (this.menuList.length == 0) {
      if (Objects.valid(goToUrl)) {
        this.router.navigate([ goToUrl ]);
      } else {
        this.router.navigate([ '/workspace' ]);
      }
      if (Objects.valid(refreshUrl)) {
        this.tab.refreshEvent.emit({
          url: refreshUrl
        });
      }
      return;
    }

    // 如果当前删除的对象是当前选中的，那么需要跳转
    if (this.currentIndex === index) {
      // 如果给定了到达路由，从列表中搜索并展现
      if (Objects.valid(goToUrl)) {
        const goToIndex: number = this.menuList.findIndex(p => p.url === goToUrl);
        if (goToIndex !== -1) {
          this.router.navigate([ this.menuList[ goToIndex ].url ]);
          return;
        }
      }
      // 显示上一个选中
      let menu = this.menuList[ index - 1 ];
      if (!menu) {// 如果上一个没有下一个选中
        menu = this.menuList[ index ];
      }
      // 跳转路由
      this.router.navigate([ menu.url ]);
    }
    if (Objects.valid(refreshUrl)) {
      console.log(refreshUrl);
      this.tab.refreshEvent.emit({
        url: refreshUrl
      });
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

export interface MenuConfig {

  url?: string;
  title?: string;
  removable?: boolean;
  replaceParams?: string[] | null;
  routeConfig?: Route;

}

export declare interface ClosableTab {

  tabClose(): void;

}

export declare interface RefreshableTab {

  refresh(): void;

}
