export const ROLE_ROUTES: Record<string, string> = {
  ROLE_ADMINISTRADOR: '/administrador',
  ROLE_COORDINADOR: '/coordinador',
  ROLE_ASESOR: '/asesor',
  ROLE_RECEPCIONISTA: '/recepcionista',
  ROLE_CIUDADANO: '/ciudadano',
};

export const getRouteByRol = (rol: string | undefined): string => {
  return rol ? ROLE_ROUTES[rol] || '/' : '/';
};
