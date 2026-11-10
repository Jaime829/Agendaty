export const authService = {
  login: async (data: { username: string; password: string; rol?: string }): Promise<any> => {
    // Login simple sin JWT: guarda el usuario en localStorage
    const user = {
      usuarioId: 1,
      username: data.username,
      rol: data.rol ?? 'ROLE_RECEPCIONISTA',
    };
    localStorage.setItem('token', 'demo-token');
    localStorage.setItem('user', JSON.stringify(user));
    return { token: 'demo-token', ...user };
  },
  setToken: (token: string) => localStorage.setItem('token', token),
  getToken: () => localStorage.getItem('token'),
  removeToken: () => localStorage.removeItem('token'),
  getUser: () => {
    const raw = localStorage.getItem('user');
    return raw ? JSON.parse(raw) : null;
  },
  setUser: (user: any) => localStorage.setItem('user', JSON.stringify(user)),
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
};
