import http from './http';

const unwrap = (data) => (Array.isArray(data?.content) ? data.content : data);

const deliveryService = {
  async assignDelivery(deliveryId, riderId) {
    const { data } = await http.patch(`/admin/deliveries/${deliveryId}/assign`, { riderId });
    return data;
  },
  async updateStatus(deliveryId, status) {
    const { data } = await http.post(`/deliveries/${deliveryId}/status`, { status });
    return data;
  },
  async getAllDeliveries() {
    const { data } = await http.get('/admin/deliveries');
    return unwrap(data);
  },
  async getAssignedDeliveries() {
    const { data } = await http.get('/rider/active');
    return unwrap(data);
  }
};

export default deliveryService;
