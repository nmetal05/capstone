import dayjs from 'dayjs';
import { IAppUserProfile } from 'app/shared/model/app-user-profile.model';
import { IDoctorProfile } from 'app/shared/model/doctor-profile.model';

export interface IDoctorViewHistory {
  id?: number;
  viewDate?: dayjs.Dayjs;
  user?: IAppUserProfile;
  doctor?: IDoctorProfile;
}

export const defaultValue: Readonly<IDoctorViewHistory> = {};
