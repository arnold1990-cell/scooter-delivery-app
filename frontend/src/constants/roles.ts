import type { UserRole } from '../types';

export const ROLE_PREFIX = 'ROLE_';

export const ROLES: Record<UserRole, UserRole> = {
  CUSTOMER: 'CUSTOMER',
  RIDER: 'RIDER',
  ADMIN: 'ADMIN'
};

export const normalizeRole = (role: string): UserRole | null => {
  if (!role) return null;
  const withoutPrefix = role.startsWith(ROLE_PREFIX) ? role.slice(ROLE_PREFIX.length) : role;
  const normalized = withoutPrefix.trim().toUpperCase();

  if (normalized === ROLES.CUSTOMER || normalized === ROLES.RIDER || normalized === ROLES.ADMIN) {
    return normalized;
  }

  return null;
};

export const normalizeRoles = (roles: string[] | undefined | null): UserRole[] => {
  if (!roles) return [];

  const normalized = roles
    .map((role) => normalizeRole(role))
    .filter((role): role is UserRole => Boolean(role));

  return Array.from(new Set(normalized));
};
