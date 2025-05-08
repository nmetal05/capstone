import axios from 'axios';

export interface ProfileStatus {
  authorities: string[];
  hasAppUserProfile: boolean;
  hasDoctorProfile: boolean;
  doctorProfileComplete: boolean;
}

export const getProfileStatus = async (): Promise<ProfileStatus> => {
  try {
    const response = await axios.get<ProfileStatus>('/api/profile/status');
    console.warn('Profile status response:', response.data); // For debugging
    return response.data;
  } catch (error) {
    console.error('Error fetching profile status:', error);
    throw error;
  }
};
