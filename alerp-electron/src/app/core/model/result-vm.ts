export interface ResultVM<T> {
  readonly code: number,
  readonly message: string | null,
  readonly data: T | null

  constructor(init?: {
    code?: number,
    message?: string,
    data?: T | null
  });

}
