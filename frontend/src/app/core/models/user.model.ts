export interface User {
  id: number;
  email: string;
  username: string | null;
  firstName: string;
  lastName: string;
  roles: string[];
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInSeconds: number;
  user: User;
}

export interface RegisterPayload {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface LoginPayload {
  /** Email or username. */
  login: string;
  password: string;
}

export interface UpdateAccountPayload {
  currentPassword: string;
  username: string;
  /** null = keep the current password. */
  newPassword: string | null;
}

export interface AdminUser {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  enabled: boolean;
  roles: string[];
}
