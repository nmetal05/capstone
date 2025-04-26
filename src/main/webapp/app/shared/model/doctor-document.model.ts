import dayjs from 'dayjs';
import { IDoctorProfile } from 'app/shared/model/doctor-profile.model';
import { DocumentType } from 'app/shared/model/enumerations/document-type.model';
import { VerificationStatus } from 'app/shared/model/enumerations/verification-status.model';

export interface IDoctorDocument {
  id?: number;
  type?: keyof typeof DocumentType;
  fileName?: string;
  fileContentContentType?: string;
  fileContent?: string;
  uploadDate?: dayjs.Dayjs;
  verificationStatus?: keyof typeof VerificationStatus;
  doctor?: IDoctorProfile;
}

export const defaultValue: Readonly<IDoctorDocument> = {};
