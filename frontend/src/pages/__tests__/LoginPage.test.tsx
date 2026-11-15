import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import LoginPage from '@/pages/LoginPage';

const authMock = vi.hoisted(() => ({
  login: vi.fn(),
  registrar: vi.fn(),
}));

const ctxMock = vi.hoisted(() => ({
  login: vi.fn(),
  user: null as any,
  isAuthenticated: false,
}));

vi.mock('@/api/auth', () => ({
  authService: {
    login: authMock.login,
    registrar: authMock.registrar,
    setToken: vi.fn(),
    setUser: vi.fn(),
    getToken: vi.fn(() => null),
    getUser: vi.fn(() => null),
    logout: vi.fn(),
  },
}));

vi.mock('@/context/AuthContext', () => ({
  useAuthContext: () => ({
    user: ctxMock.user,
    loading: false,
    isAuthenticated: ctxMock.isAuthenticated,
    login: ctxMock.login,
    logout: vi.fn(),
    tieneRol: () => false,
    tieneAlgunaRol: () => false,
  }),
}));

function renderLogin() {
  return render(
    <MemoryRouter initialEntries={['/login']}>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/administrador" element={<div>PANEL ADMIN</div>} />
        <Route path="/ciudadano" element={<div>PANEL CIUDADANO</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe('LoginPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    ctxMock.user = null;
    ctxMock.isAuthenticated = false;
  });

  it('renderiza los campos correo y contraseña y el botón de envío', () => {
    renderLogin();
    expect(screen.getByLabelText('Correo electrónico')).toBeInTheDocument();
    expect(screen.getByLabelText('Contraseña')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /iniciar sesión/i })).toBeInTheDocument();
  });

  it('redirige al panel del rol cuando ya hay sesión activa', () => {
    ctxMock.user = { usuarioId: 3, username: 'juan@email.com', rol: 'ROLE_CIUDADANO' };
    ctxMock.isAuthenticated = true;
    renderLogin();
    expect(screen.getByText('PANEL CIUDADANO')).toBeInTheDocument();
  });

  it('muestra el error cuando las credenciales son inválidas', async () => {
    const user = userEvent.setup();
    authMock.login.mockRejectedValueOnce(new Error('Credenciales inválidas'));
    renderLogin();

    await user.type(screen.getByLabelText('Correo electrónico'), 'admin@turnos.local');
    await user.type(screen.getByLabelText('Contraseña'), 'mal-clave');
    await user.click(screen.getByRole('button', { name: /iniciar sesión/i }));

    expect(await screen.findByText('Credenciales inválidas')).toBeInTheDocument();
    expect(ctxMock.login).not.toHaveBeenCalled();
  });

  it('en caso de éxito guarda la sesión y la envía al contexto', async () => {
    const user = userEvent.setup();
    authMock.login.mockResolvedValueOnce({ id: 1, correo: 'admin@turnos.local', rol: 'ROLE_ADMINISTRADOR', token: undefined });
    renderLogin();

    await user.type(screen.getByLabelText('Correo electrónico'), 'admin@turnos.local');
    await user.type(screen.getByLabelText('Contraseña'), 'Admin123*');
    await user.click(screen.getByRole('button', { name: /iniciar sesión/i }));

    expect(ctxMock.login).toHaveBeenCalledWith(
      expect.objectContaining({ usuarioId: 1, rol: 'ROLE_ADMINISTRADOR' })
    );
  });
});
