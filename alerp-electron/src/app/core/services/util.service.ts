export class StringUtils {

  public static isEmpty(str: string): boolean {
    return str === undefined || str === null || str.length === 0;
  }

}

export class Objects {

  public static valid(obj: any): boolean {
    return obj !== undefined && obj !== null;
  }

  public static isNaN(value: any): boolean {
    if(!Objects.valid(value)){
      return true;
    }
    if (value === '') {
      return true;
    }
    return isNaN(value);
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

  public static now() {
    return DateUtils.format(new Date());
  }

  public static of(year: number, month: number, date?: number, hours?: number, minutes?: number, seconds?: number) {
    const d: Date = new Date(year, month, date ? date : 1, hours ? hours : 0, minutes ? minutes : 0, seconds ? seconds : 0);
    return DateUtils.format(d);
  }

}

export class SpecificationUtils {

  static NUM_PATT = /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/i;
  static FAI_U = 'Φ';
  static FAI_L = 'φ';

  public static convert(str: string): number[] {
    if (StringUtils.isEmpty(str)) {
      return null;
    }
    str = str.replace(/ /g, '');
    const splits: string[] = str.split('*');
    if (splits.length < 2 || splits.length > 3) {
      return null;
    }
    if (splits.length === 2) {
      let diameterStr: string;
      let heightStr: string;
      if (splits[0].startsWith(SpecificationUtils.FAI_U) || splits[0].startsWith(SpecificationUtils.FAI_L)) {
        diameterStr = splits[0].substring(1);
        heightStr = splits[1];
      } else {
        return null;
      }
      if (diameterStr.search(SpecificationUtils.NUM_PATT) === -1) {
        return null;
      }
      if (heightStr.search(SpecificationUtils.NUM_PATT) === -1) {
        return null;
      }
      return [parseFloat(diameterStr), parseFloat(heightStr)];
    }
    if (splits.length === 3) {
      for (const s of splits) {
        if (s.search(SpecificationUtils.NUM_PATT) === -1) {
          return null;
        }
      }
      return splits.map(s => {
        return parseFloat(s);
      });
    }
    return null;
  }

  public static valid(str: string): boolean {
    return SpecificationUtils.convert(str) !== null;
  }

  public static calculateWeight(spec: string, density: number, quantity: number): number {
    let specArr: number[] = SpecificationUtils.convert(spec);
    if (!Objects.valid(specArr) || Objects.isNaN(density) || Objects.isNaN(quantity)) {
      return 0;
    }
    let volumn: number;
    if (specArr.length === 2) {
      volumn = Math.PI * Math.pow(specArr[0] / 2.0, 2) * specArr[1];
    } else {
      volumn = specArr[0] * specArr[1] * specArr[2];
    }
    return parseFloat((volumn * density * quantity).toFixed(2));
  }

}
