import { IDoctorProfile } from 'app/shared/model/doctor-profile.model';

export interface ISpecialization {
  id?: number;
  name?: string;
  description?: string | null;
  doctorProfiles?: IDoctorProfile[] | null;
}

export const defaultValue: Readonly<ISpecialization> = {};
