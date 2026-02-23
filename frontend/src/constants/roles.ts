import type { Authority, UserRole } from '../types';

export const ROLE_PREFIX = 'ROLE_';

export const ROLES: Record<UserRole, UserRole> = {
  CUSTOMER: 'CUSTOMER',
  RIDER: 'RIDER',
  ADMIN: 'ADMIN'
};

export const AUTHORITIES: Record<UserRole, Authority> = {
  CUSTOMER: 'ROLE_CUSTOMER',
  RIDER: 'ROLE_RIDER',
  ADMIN: 'ROLE_ADMIN'
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

export const normalizeAuthority = (authority: string): Authority | null => {
  const normalizedRole = normalizeRole(authority);
  if (!normalizedRole) return null;
  return AUTHORITIES[normalizedRole];
};

export const normalizeRoles = (roles: string[] | undefined | null): UserRole[] => {
  if (!roles) return [];

  const normalized = roles
    .map((role) => normalizeRole(role))
    .filter((role): role is UserRole => Boolean(role));

  return Array.from(new Set(normalized));
};

export const normalizeAuthorities = (authorities: string[] | undefined | null): Authority[] => {
  if (!authorities) return [];

  const normalized = authorities
    .map((authority) => normalizeAuthority(authority))
    .filter((authority): authority is Authority => Boolean(authority));

  return Array.from(new Set(normalized));
};
