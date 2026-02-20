import { api } from './client';
import type { Delivery, DeliveryStatus } from '../types';

const deliveriesApi = {
  async create(payload: {
    pickupAddress: string;
    dropoffAddress: string;
    pickupLatitude: number;
    pickupLongitude: number;
    dropoffLatitude: number;
    dropoffLongitude: number;
    price?: number;
    notes?: string;
  }) {
    const { data } = await api.post<Delivery>('/deliveries', payload);
    return data;
  },
  async my() {
    const { data } = await api.get<Delivery[]>('/deliveries/my');
    return data;
  },
  async jobs() {
    const { data } = await api.get<Delivery[]>('/rider/jobs');
    return data;
  },
  async active() {
    const { data } = await api.get<Delivery[]>('/rider/active');
    return data;
  },
  async updateStatus(id: string, status: DeliveryStatus) {
    const { data } = await api.post<Delivery>(`/deliveries/${id}/status`, { status });
    return data;
  },
  async adminAll() {
    const { data } = await api.get('/admin/deliveries');
    return data.content as Delivery[];
  }
};

export default deliveriesApi;
