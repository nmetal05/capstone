import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { ISpecialization } from 'app/shared/model/specialization.model';

export interface IDoctorProfile {
  id?: string;
  phoneNumber?: string;
  officeAddress?: string;
  latitude?: number;
  longitude?: number;
  inpeCode?: string;
  isVerified?: boolean;
  lastLoginIp?: string | null;
  lastUserAgent?: string | null;
  lastLoginDate?: dayjs.Dayjs | null;
  internalUser?: IUser | null;
  specializations?: ISpecialization[] | null;
}

export const defaultValue: Readonly<IDoctorProfile> = {
  isVerified: false,
};
