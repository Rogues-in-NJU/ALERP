export class StringUtils {

  public static isEmpty(str: string): boolean {
    return str === undefined || str === null || str.length === 0;
  }

}

export class Objects {

  public static valid(obj: any): boolean {
    return obj !== undefined && obj !== null;
  }

}

export class DateUtils {

  public static compare(date1: Date, date2: Date): number {
    return date1.getTime() - date2.getTime();
  }

  public static format(date: Date, fmt?: string): string {
    if (!Objects.valid(fmt)) {
      fmt = 'yyyy-MM-dd HH:mm';
    }
    let o: any = {
      "M+": date.getMonth() + 1,                 //月份
      "d+": date.getDate(),                    //日
      "H+": date.getHours(),                   //小时
      "m+": date.getMinutes(),                 //分
      "s+": date.getSeconds(),                 //秒
      "q+": Math.floor((date.getMonth() + 3) / 3), //季度
      "S": date.getMilliseconds()             //毫秒
    };
    if (/(y+)/.test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (const k in o) {
      if (new RegExp("(" + k + ")").test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[ k ]) : (("00" + o[ k ]).substr(("" + o[ k ]).length)));
      }
    }
    return fmt;
  }

}
