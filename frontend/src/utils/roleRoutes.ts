// Fuente única del panel de destino por rol — úsese en navbar, landing, login y guards.
export const getHomeByRole = (rol: string | undefined): string => {
  switch (rol) {
    case 'ROLE_ADMINISTRADOR':
      return '/administrador';
    case 'ROLE_COORDINADOR':
      return '/coordinador';
    case 'ROLE_ASESOR':
      return '/asesor';
    case 'ROLE_RECEPCIONISTA':
      return '/recepcionista';
    case 'ROLE_CIUDADANO':
      return '/ciudadano';
    default:
      return '/';
  }
};
