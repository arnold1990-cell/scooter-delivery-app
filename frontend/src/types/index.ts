export type UserRole = 'CUSTOMER' | 'RIDER' | 'ADMIN';

export interface AuthUser {
  email: string;
  fullName: string;
  role: UserRole;
  token: string;
}

export type DeliveryStatus =
  | 'CREATED'
  | 'ASSIGNED'
  | 'ACCEPTED'
  | 'IN_PROGRESS'
  | 'DELIVERED'
  | 'CANCELLED';

export interface Delivery {
  id: string;
  customerId: string;
  riderId?: string;
  pickupAddress: string;
  dropoffAddress: string;
  price: number;
  status: DeliveryStatus;
  notes?: string;
  createdAt: string;
}

export type ApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface RiderProfile {
  id: string;
  userId: string;
  licenseNumber?: string;
  approvalStatus: ApprovalStatus;
  isOnline: boolean;
  fullName?: string;
  email?: string;
}
