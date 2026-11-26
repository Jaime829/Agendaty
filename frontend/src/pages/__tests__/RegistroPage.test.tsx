import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import RegistroPage from '@/pages/RegistroPage';

const authMock = vi.hoisted(() => ({
  registrar: vi.fn(),
}));

vi.mock('@/api/auth', () => ({
  authService: {
    login: vi.fn(),
    registrar: authMock.registrar,
    setToken: vi.fn(),
    setUser: vi.fn(),
    getToken: vi.fn(() => null),
    getUser: vi.fn(() => null),
    logout: vi.fn(),
  },
}));

const ctxMock = vi.hoisted(() => ({
  user: null as any,
  isAuthenticated: false,
}));

vi.mock('@/context/AuthContext', () => ({
  useAuthContext: () => ({
    user: ctxMock.user,
    loading: false,
    isAuthenticated: ctxMock.isAuthenticated,
    login: vi.fn(),
    logout: vi.fn(),
    tieneRol: () => false,
    tieneAlgunaRol: () => false,
  }),
}));

function renderRegistro() {
  return render(
    <MemoryRouter initialEntries={['/registro']}>
      <Routes>
        <Route path="/registro" element={<RegistroPage />} />
        <Route path="/login" element={<div>PANTALLA LOGIN</div>} />
        <Route path="/administrador" element={<div>PANEL ADMIN</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe('RegistroPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    ctxMock.user = null;
    ctxMock.isAuthenticated = false;
  });

  it('redirige al panel del rol cuando ya hay sesión activa', () => {
    ctxMock.user = { usuarioId: 1, username: 'admin@turnos.local', rol: 'ROLE_ADMINISTRADOR' };
    ctxMock.isAuthenticated = true;
    renderRegistro();
    expect(screen.getByText('PANEL ADMIN')).toBeInTheDocument();
  });

  it('renderiza todos los campos del formulario', () => {
    renderRegistro();
    expect(screen.getByLabelText('Nombre completo')).toBeInTheDocument();
    expect(screen.getByLabelText('Correo electrónico')).toBeInTheDocument();
    expect(screen.getByLabelText('Documento de identidad')).toBeInTheDocument();
    expect(screen.getByLabelText('Teléfono')).toBeInTheDocument();
    expect(screen.getByLabelText('Contraseña')).toBeInTheDocument();
  });

  it('envía los datos al servicio de registro', async () => {
    const user = userEvent.setup();
    authMock.registrar.mockResolvedValueOnce({ id: 7, correo: 'juan@email.com', rol: 'ROLE_CIUDADANO' });
    renderRegistro();

    await user.type(screen.getByLabelText('Nombre completo'), 'Juan Pérez');
    await user.type(screen.getByLabelText('Correo electrónico'), 'juan@email.com');
    await user.type(screen.getByLabelText('Documento de identidad'), '12345678');
    await user.type(screen.getByLabelText('Teléfono'), '3001234567');
    await user.type(screen.getByLabelText('Contraseña'), 'Secreta1*');
    await user.click(screen.getByRole('button', { name: /crear cuenta/i }));

    expect(authMock.registrar).toHaveBeenCalledWith({
      nombre: 'Juan Pérez',
      correo: 'juan@email.com',
      documento: '12345678',
      telefono: '3001234567',
      password: 'Secreta1*',
    });
  });

  it('muestra errores de campo devueltos por el backend', async () => {
    const user = userEvent.setup();
    const error = new Error('Error de validación') as any;
    error.fieldErrors = { correo: 'El correo ya está registrado' };
    authMock.registrar.mockRejectedValueOnce(error);
    renderRegistro();

    await user.type(screen.getByLabelText('Nombre completo'), 'Juan Pérez');
    await user.type(screen.getByLabelText('Correo electrónico'), 'juan@email.com');
    await user.type(screen.getByLabelText('Documento de identidad'), '12345678');
    await user.type(screen.getByLabelText('Teléfono'), '3001234567');
    await user.type(screen.getByLabelText('Contraseña'), 'Secreta1*');
    await user.click(screen.getByRole('button', { name: /crear cuenta/i }));

    expect(await screen.findByText(/El correo ya está registrado/)).toBeInTheDocument();
  });
});
