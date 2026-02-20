import { api } from './client';
import type { ApprovalStatus, RiderProfile } from '../types';

const ridersApi = {
  async me() {
    const { data } = await api.get<RiderProfile>('/riders/me');
    return data;
  },
  async toggleOnline(online: boolean) {
    const { data } = await api.patch<RiderProfile>('/riders/me/online', { online });
    return data;
  },
  async all() {
    const { data } = await api.get<RiderProfile[]>('/admin/riders');
    return data;
  },
  async approve(userId: string, status: ApprovalStatus) {
    const { data } = await api.patch<RiderProfile>(`/admin/riders/${userId}/approve`, { status });
    return data;
  }
};

export default ridersApi;
