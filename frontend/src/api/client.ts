import axios, { AxiosError } from 'axios';

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

export const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
});

// Mapea errores de la API a mensajes claros
export class ApiClientError extends Error {
  status: number;
  details: string;
  constructor(message: string, status: number, details: string) {
    super(message);
    this.status = status;
    this.details = details;
  }
}

api.interceptors.response.use(
  (res) => res,
  (err: AxiosError) => {
    if (err.response) {
      const { status, data } = err.response;
      const body = data as any;
      const message = body?.errorMessage || body?.message || `Error ${status}`;
      const details = body?.errorsDetails || '';
      throw new ApiClientError(message, status as number, details);
    }
    throw err;
  }
);

export default api;
