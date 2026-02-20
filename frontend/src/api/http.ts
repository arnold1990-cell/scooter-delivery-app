import axios from 'axios';

export { api as default } from './client';

export const isHttpError = (error: unknown) => axios.isAxiosError(error);
