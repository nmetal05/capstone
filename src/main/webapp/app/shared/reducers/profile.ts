import { createAsyncThunk, createSlice, PayloadAction, createAction, isAnyOf } from '@reduxjs/toolkit';
import { getProfileStatus, ProfileStatus } from 'app/shared/services/profile.service';

/* --------------------------------------------------------------------
   State
   -------------------------------------------------------------------- */
export interface ProfileState extends ProfileStatus {
  loading: boolean;
  errorMessage: string | null;
}

const initialState: ProfileState = {
  authorities: [],
  hasAppUserProfile: false,
  hasDoctorProfile: false,
  doctorProfileComplete: false,
  loading: false,
  errorMessage: null,
};

/* --------------------------------------------------------------------
   Thunks
   -------------------------------------------------------------------- */
export const fetchProfileStatus = createAsyncThunk('profile/fetchStatus', async () => await getProfileStatus());

/* --------------------------------------------------------------------
   Local setters   (NEW)
   -------------------------------------------------------------------- */
export const setHasDoctorProfile = createAction<boolean>('profile/setHasDoctorProfile');
export const setDoctorProfileComplete = createAction<boolean>('profile/setDoctorProfileComplete');

/* --------------------------------------------------------------------
   Slice
   -------------------------------------------------------------------- */
export const profileSlice = createSlice({
  name: 'profile',
  initialState,
  reducers: {},
  extraReducers(builder) {
    /* async thunk ---------------------------------------------------- */
    builder
      .addCase(fetchProfileStatus.pending, state => {
        state.loading = true;
        state.errorMessage = null;
      })
      .addCase(fetchProfileStatus.fulfilled, (state, action: PayloadAction<ProfileStatus>) => {
        state.loading = false;
        Object.assign(state, action.payload);
      })
      .addCase(fetchProfileStatus.rejected, (state, action) => {
        state.loading = false;
        state.errorMessage = action.error.message ?? 'Error loading profile status';
      });

    /* local setters -------------------------------------------------- */
    builder.addMatcher(isAnyOf(setHasDoctorProfile, setDoctorProfileComplete), (state, action) => {
      if (setHasDoctorProfile.match(action)) state.hasDoctorProfile = action.payload;
      if (setDoctorProfileComplete.match(action)) state.doctorProfileComplete = action.payload;
    });
  },
});

export default profileSlice.reducer;
