export interface ResultVO<T> {

  readonly code: number;
  readonly message: string | null;
  readonly data?: T | null;

}

export interface TableResultVO<T> {

  readonly totalPages: number;
  readonly pageIndex: number;
  readonly pageSize: number;
  readonly result?: T[] | null;

}

export interface TableQueryParams {

  pageIndex: number | 1;
  pageSize: number | 10;

  [name: string]: any;

}
