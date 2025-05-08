import axios from 'axios';
import { Storage } from 'react-jhipster';
import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { AppThunk } from 'app/config/store';
import { setLocale } from 'app/shared/reducers/locale';
import { serializeAxiosError } from './reducer.utils';
import { AUTHORITIES } from 'app/config/constants';

/* --------------------------------------------------------------------
   State
   -------------------------------------------------------------------- */
export const initialState = {
  loading: false,
  isAuthenticated: false,
  account: {} as any,
  errorMessage: null as unknown as string,
  redirectMessage: null as unknown as string,
  sessionHasBeenFetched: false,
  logoutUrl: null as unknown as string,
  roleSelectionCompleted: false,
  selectedRole: null as string | null,
};

export type AuthenticationState = Readonly<typeof initialState>;

/* --------------------------------------------------------------------
   Thunks
   -------------------------------------------------------------------- */
export const getAccount = createAsyncThunk('authentication/get_account', async () => axios.get<any>('api/account'), {
  serializeError: serializeAxiosError,
});

export const logoutServer = createAsyncThunk('authentication/logout', async () => axios.post<any>('api/logout', {}), {
  serializeError: serializeAxiosError,
});

export const setUserRole = createAsyncThunk(
  'authentication/set_role',
  async (role: string, { rejectWithValue }) => {
    try {
      await axios.post('/api/profile/choose-type', { type: role });
      return role;
    } catch (err: any) {
      return rejectWithValue(err.response?.data || err.message);
    }
  },
  { serializeError: serializeAxiosError },
);

/* --------------------------------------------------------------------
   Thunk helpers referenced elsewhere
   -------------------------------------------------------------------- */
export const getSession = (): AppThunk => async (dispatch, getState) => {
  await dispatch(getAccount());
  const { account } = getState().authentication;
  if (account?.langKey) {
    const langKey = Storage.session.get('locale', account.langKey);
    await dispatch(setLocale(langKey));
  }
};

export const logout: () => AppThunk = () => async dispatch => {
  await dispatch(logoutServer());
  dispatch(getSession());
};

export const clearAuthentication = (messageKey: string) => dispatch => {
  dispatch(authError(messageKey));
  dispatch(clearAuth());
};

/* --------------------------------------------------------------------
   Slice
   -------------------------------------------------------------------- */
export const AuthenticationSlice = createSlice({
  name: 'authentication',
  initialState: initialState as AuthenticationState,
  reducers: {
    authError(state, action: PayloadAction<string>) {
      state.redirectMessage = action.payload;
    },
    clearAuth(state) {
      Object.assign(state, { ...initialState, logoutUrl: state.logoutUrl });
    },
    /* onboarding finished (called in CompleteDoctorProfile) */
    completeRoleSelection(state, action: PayloadAction<string>) {
      state.roleSelectionCompleted = true;
      state.selectedRole = action.payload;
    },
    /* provisional choice made on /choose-profile */
    setSelectedRole(state, action: PayloadAction<string>) {
      state.selectedRole = action.payload;
    },
  },
  extraReducers(builder) {
    builder
      /* getAccount --------------------------------------------------- */
      .addCase(getAccount.pending, state => {
        state.loading = true;
      })
      .addCase(getAccount.fulfilled, (state, action) => {
        const account = action.payload.data;
        const isAdmin = account.authorities?.includes(AUTHORITIES.ADMIN);
        const hasRole = account.authorities?.some(a => [AUTHORITIES.DOCTOR, AUTHORITIES.APP_USER].includes(a));
        const computed = isAdmin
          ? AUTHORITIES.ADMIN
          : account.authorities?.find(a => [AUTHORITIES.DOCTOR, AUTHORITIES.APP_USER].includes(a)) || null;

        state.loading = false;
        state.sessionHasBeenFetched = true;
        state.account = account;
        state.isAuthenticated = !!account.activated;

        /* ---- KEEP true values we already had ---------------------- */
        state.roleSelectionCompleted = state.roleSelectionCompleted || isAdmin || hasRole;
        state.selectedRole = computed || state.selectedRole;
      })
      .addCase(getAccount.rejected, (state, action) => {
        state.loading = false;
        state.isAuthenticated = false;
        state.sessionHasBeenFetched = true;
        state.errorMessage = action.error.message;
        state.roleSelectionCompleted = false;
        state.selectedRole = null;
      })
      /* logoutServer -------------------------------------------------- */
      .addCase(logoutServer.fulfilled, (state, action) => {
        Object.assign(state, initialState);
        state.logoutUrl = action.payload.data.logoutUrl;
      })
      /* setUserRole (APP_USER) --------------------------------------- */
      .addCase(setUserRole.fulfilled, (state, action) => {
        state.roleSelectionCompleted = true;
        state.selectedRole = action.payload;
      })
      .addCase(setUserRole.rejected, (state, action) => {
        state.errorMessage = action.payload as string;
      });
  },
});

/* --------------------------------------------------------------------
   Exports
   -------------------------------------------------------------------- */
export const { authError, clearAuth, completeRoleSelection, setSelectedRole } = AuthenticationSlice.actions;

export default AuthenticationSlice.reducer;
