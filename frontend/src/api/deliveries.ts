import http from './http';
import type { Delivery, DeliveryStatus } from '../types';

const deliveriesApi = {
  async create(payload: { pickupAddress: string; dropoffAddress: string; price: number; notes?: string }) {
    const { data } = await http.post<Delivery>('/api/deliveries', payload);
    return data;
  },
  async my() {
    const { data } = await http.get<Delivery[]>('/api/deliveries/my');
    return data;
  },
  async jobs() {
    const { data } = await http.get<Delivery[]>('/api/rider/jobs');
    return data;
  },
  async active() {
    const { data } = await http.get<Delivery[]>('/api/rider/active');
    return data;
  },
  async updateStatus(id: string, status: DeliveryStatus) {
    const { data } = await http.patch<Delivery>(`/api/deliveries/${id}/status`, { status });
    return data;
  },
  async adminAll() {
    const { data } = await http.get('/api/admin/deliveries');
    return data.content as Delivery[];
  }
};

export default deliveriesApi;
