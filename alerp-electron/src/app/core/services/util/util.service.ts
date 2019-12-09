export class StringUtils {
  public static isEmpty(str: string): boolean {
    return str === undefined || str === null || str.length === 0;
  }
}
