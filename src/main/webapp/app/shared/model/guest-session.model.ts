import dayjs from 'dayjs';

export interface IGuestSession {
  id?: number;
  sessionId?: string;
  createdAt?: dayjs.Dayjs;
  lastActiveAt?: dayjs.Dayjs;
  ipAddress?: string;
  userAgent?: string;
}

export const defaultValue: Readonly<IGuestSession> = {};
