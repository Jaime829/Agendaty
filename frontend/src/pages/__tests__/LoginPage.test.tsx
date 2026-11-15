import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import LoginPage from '@/pages/LoginPage';

const authMock = vi.hoisted(() => ({
  login: vi.fn(),
  registrar: vi.fn(),
}));

const ctxMock = vi.hoisted(() => ({
  login: vi.fn(),
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
    user: null,
    loading: false,
    isAuthenticated: false,
    login: ctxMock.login,
    logout: vi.fn(),
    tieneRol: () => false,
    tieneAlgunaRol: () => false,
  }),
}));

function renderLogin() {
  return render(
    <MemoryRouter initialEntries={['/login']}>
      <LoginPage />
    </MemoryRouter>
  );
}

describe('LoginPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renderiza los campos correo y contraseña y el botón de envío', () => {
    renderLogin();
    expect(screen.getByLabelText('Correo electrónico')).toBeInTheDocument();
    expect(screen.getByLabelText('Contraseña')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /iniciar sesión/i })).toBeInTheDocument();
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
