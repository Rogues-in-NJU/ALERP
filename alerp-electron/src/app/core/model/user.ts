export interface UserInfoVO {

  id?: string;
  name?: string;
  phoneNumber?: string;
  password?: string;

  auth?: Auth[];

}

export interface Auth {

  id?: string;
  route?: string;
  description?: string;
  action?: number;

}

export interface PassportVO {

  phoneNumber: string;
  password: string;
  city: number;

}

export interface LoginResultVO {

  userId: number;
  code: number;
  result: string;

}

export interface UserStorageVO {

  id: number;
  city: number;

}
