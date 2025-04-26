import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface IAppUserProfile {
  id?: string;
  latitude?: number | null;
  longitude?: number | null;
  lastLoginIp?: string | null;
  lastUserAgent?: string | null;
  lastLoginDate?: dayjs.Dayjs | null;
  internalUser?: IUser | null;
}

export const defaultValue: Readonly<IAppUserProfile> = {};
