import http from './http';
import type { ApprovalStatus, RiderProfile } from '../types';

const ridersApi = {
  async me() {
    const { data } = await http.get<RiderProfile>('/riders/me');
    return data;
  },
  async toggleOnline(online: boolean) {
    const { data } = await http.patch<RiderProfile>('/riders/me/online', { online });
    return data;
  },
  async all() {
    const { data } = await http.get<RiderProfile[]>('/admin/riders');
    return data;
  },
  async approve(userId: string, status: ApprovalStatus) {
    const { data } = await http.patch<RiderProfile>(`/admin/riders/${userId}/approve`, { status });
    return data;
  }
};

export default ridersApi;
