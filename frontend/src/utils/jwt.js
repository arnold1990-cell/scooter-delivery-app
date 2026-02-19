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

export function extractRoleFromToken(token) {
  const payload = decodeJwt(token);
  if (!payload) return null;

  if (typeof payload.role === 'string') return payload.role;

  if (Array.isArray(payload.roles) && payload.roles.length > 0) {
    return payload.roles[0];
  }

  if (Array.isArray(payload.authorities) && payload.authorities.length > 0) {
    const firstAuthority = payload.authorities[0];
    return typeof firstAuthority === 'string' ? firstAuthority.replace('ROLE_', '') : null;
  }

  return null;
}
