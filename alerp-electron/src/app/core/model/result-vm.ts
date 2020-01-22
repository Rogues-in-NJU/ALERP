export class ResultVO<T> {

  readonly code: number;
  readonly message: string | null;
  readonly data?: T | null;

}

export class TableResultVO<T> {

  readonly totalPages: number;
  readonly pageIndex: number;
  readonly pageSize: number;
  readonly result?: T[] | null;

}

export class QueryParams {

  [ name: string ]: any;

}

export class TableQueryParams {

  pageIndex: number | 1;
  pageSize: number | 10;

  [ name: string ]: any;

}

export class ResultCode {

  public static SUCCESS: ResultCode = new ResultCode(10000, '成功');
  public static SERVER_ERROR: ResultCode = new ResultCode(10001, '系统错误');
  public static SERVER_TIMEOUT: ResultCode = new ResultCode(10002, '服务超时');
  public static SERVER_LIMIT: ResultCode = new ResultCode(10003, '服务限流');
  public static ILLEGAL_PARAM: ResultCode = new ResultCode(10004, '参数错误');
  public static API_NOT_EXIST: ResultCode = new ResultCode(10005, '接口不存在');
  public static HTTP_METHOD_NOT_SUPPORT: ResultCode = new ResultCode(10006, 'HTTP METHOD不支持');
  public static ILLEGAL_REQUEST: ResultCode = new ResultCode(10007, '非法请求');
  public static ILLEGAL_USER: ResultCode = new ResultCode(10008, '非法用户');
  public static AUTH_FAIL: ResultCode = new ResultCode(10009, '没有权限');
  public static OTHER_ERROR: ResultCode = new ResultCode(20000, '其他异常');
  public static FIELD_EDIT_FORBID: ResultCode = new ResultCode(20001, '禁止更改字段');

  private _code: number;
  private  _message: string;

  private constructor(code: number, message: string) {
    this._code = code;
    this._message = message;
  }

  get code(): number {
    return this._code;
  }

  get message(): string {
    return this._message;
  }

}

export class LoginCode {

  public static SUCCESS: LoginCode = new LoginCode(1, '登录成功');
  public static INCORRECT: LoginCode = new LoginCode(2, '密码错误');
  public static NONE: LoginCode = new LoginCode(3, '没有该手机号的用户');
  public static DENIED: LoginCode = new LoginCode(4, '没有该城市访问权限');
  public static OFFJOB: LoginCode = new LoginCode(5, '用户已离职');
  public static DUPLICATE: LoginCode = new LoginCode(6, '重复登录');

  private _code: number;
  private _message: string;

  private constructor(code: number, message: string) {
    this._code = code;
    this._message = message;
  }

  get code(): number {
    return this._code;
  }

}
