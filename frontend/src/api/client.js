import axios from 'axios';

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 12000,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const session = localStorage.getItem('fastpass_session');
  if (session) {
    const { accessToken } = JSON.parse(session);
    if (accessToken) config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !error.config?.url?.includes('/auth/login')) {
      localStorage.removeItem('fastpass_session');
      window.dispatchEvent(new Event('fastpass:unauthorized'));
    }
    return Promise.reject(error);
  },
);

export function apiMessage(error, fallback = 'Something went wrong') {
  const body = error?.response?.data;
  if (body?.fieldErrors?.length) return body.fieldErrors.map((item) => item.message).join(', ');
  return body?.message || fallback;
}

export default api;
