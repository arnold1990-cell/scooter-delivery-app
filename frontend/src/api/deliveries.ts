import http from './http';
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
    const { data } = await http.post<Delivery>('/deliveries', payload);
    return data;
  },
  async my() {
    const { data } = await http.get<Delivery[]>('/deliveries/my');
    return data;
  },
  async jobs() {
    const { data } = await http.get<Delivery[]>('/rider/jobs');
    return data;
  },
  async active() {
    const { data } = await http.get<Delivery[]>('/rider/active');
    return data;
  },
  async updateStatus(id: string, status: DeliveryStatus) {
    const { data } = await http.post<Delivery>(`/deliveries/${id}/status`, { status });
    return data;
  },
  async adminAll() {
    const { data } = await http.get('/admin/deliveries');
    return data.content as Delivery[];
  }
};

export default deliveriesApi;
