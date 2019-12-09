import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import { AppConfig } from './environments/environment';

if (AppConfig.production) {
  enableProdMode();
}

platformBrowserDynamic()
  .bootstrapModule(AppModule, {
    preserveWhitespaces: false
  })
  .catch(err => console.error(err));

// /**
//  * is electron
//  */
// const remote = require('electron').remote;
//
// const { Menu, MenuItem } = remote;
//
// const menu = new Menu();
// menu.append(new MenuItem({ label: '撤销', role: 'undo' }));
// menu.append(new MenuItem({ label: '重做', role: 'redo' }));
// menu.append(new MenuItem({ type: 'separator' }));
// menu.append(new MenuItem({ label: '剪切', role: 'cut' }));
// menu.append(new MenuItem({ label: '复制', role: 'copy' }));
// menu.append(new MenuItem({ label: '粘贴', role: 'paste' }));
// menu.append(new MenuItem({ label: '删除', role: 'delete' }));
// menu.append(new MenuItem({ label: '全选', role: 'selectAll' }));
//
// window.addEventListener('contextmenu', (e) => {
//   e.preventDefault();
//   menu.popup({ window: remote.getCurrentWindow() });
// }, false);

/**
 * 判断点击区域可编辑
 * @param {*} e
 */
function isElementEditable(e) {
  if (!e) {
    return false;
  }
  //为input标签或者contenteditable属性为true
  if (e.tagName == 'INPUT' || e.contentEditable == 'true') {
    return true;
  } else {
    //递归查询父节点
    return isElementEditable(e.parentNode)
  }
}
