export function decodeJwt(token) {
  if (!token) return null;
  try {
    const payload = token.split('.')[1];
    if (!payload) return null;
    const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
    const json = decodeURIComponent(
      atob(normalized)
        .split('')
        .map((c) => `%${`00${c.charCodeAt(0).toString(16)}`.slice(-2)}`)
        .join('')
    );
    return JSON.parse(json);
  } catch {
    return null;
  }
}

export function extractRolesFromToken(token) {
  const payload = decodeJwt(token);
  if (!payload) return [];

  if (Array.isArray(payload.roles)) {
    return payload.roles.filter((role) => typeof role === 'string');
  }

  if (typeof payload.role === 'string') {
    return [payload.role];
  }

  if (Array.isArray(payload.authorities)) {
    return payload.authorities.filter((authority) => typeof authority === 'string');
  }

  return [];
}

export function tokenHasRole(token, role) {
  return extractRolesFromToken(token).includes(role);
}

export function extractRoleFromToken(token) {
  const roles = extractRolesFromToken(token);
  if (roles.length === 0) return null;
  return roles[0].replace('ROLE_', '');
}
