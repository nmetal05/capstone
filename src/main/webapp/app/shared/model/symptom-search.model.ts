import dayjs from 'dayjs';
import { IAppUserProfile } from 'app/shared/model/app-user-profile.model';
import { IGuestSession } from 'app/shared/model/guest-session.model';

export interface ISymptomSearch {
  id?: number;
  searchDate?: dayjs.Dayjs;
  symptoms?: string;
  aiResponseJson?: string;
  user?: IAppUserProfile | null;
  guestSession?: IGuestSession | null;
}

export const defaultValue: Readonly<ISymptomSearch> = {};
