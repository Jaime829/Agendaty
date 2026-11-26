import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { ProtectedRoute } from '@/routes/ProtectedRoute';

const estado = vi.hoisted(() => ({
  user: null as any,
  loading: false,
  isAuthenticated: false,
}));

vi.mock('@/context/AuthContext', () => ({
  useAuthContext: () => ({
    user: estado.user,
    loading: estado.loading,
    isAuthenticated: estado.isAuthenticated,
    login: vi.fn(),
    logout: vi.fn(),
    tieneRol: (rol: string) => estado.user?.rol === rol,
    tieneAlgunaRol: (roles: string[]) => roles.includes(estado.user?.rol),
  }),
}));

function renderApp() {
  return render(
    <MemoryRouter initialEntries={['/privado']}>
      <Routes>
        <Route path="/login" element={<div>PANTALLA LOGIN</div>} />
        <Route path="/dashboard-propio" element={<div>PANTALLA PROPIA</div>} />
        <Route
          path="/privado"
          element={
            <ProtectedRoute roles={['ROLE_ADMINISTRADOR']}>
              <div>CONTENIDO PRIVADO</div>
            </ProtectedRoute>
          }
        />
      </Routes>
    </MemoryRouter>
  );
}

describe('ProtectedRoute', () => {
  beforeEach(() => {
    estado.user = null;
    estado.loading = false;
    estado.isAuthenticated = false;
  });

  it('muestra estado de carga mientras restaura la sesión', () => {
    estado.loading = true;
    estado.isAuthenticated = false;
    renderApp();
    expect(screen.queryByText('CONTENIDO PRIVADO')).not.toBeInTheDocument();
    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  it('redirige a /login cuando no hay sesión', () => {
    renderApp();
    expect(screen.getByText('PANTALLA LOGIN')).toBeInTheDocument();
    expect(screen.queryByText('CONTENIDO PRIVADO')).not.toBeInTheDocument();
  });

  it('renderiza los children cuando el rol está permitido', () => {
    estado.user = { usuarioId: 1, rol: 'ROLE_ADMINISTRADOR', correo: 'admin@turnos.local' };
    estado.isAuthenticated = true;
    renderApp();
    expect(screen.getByText('CONTENIDO PRIVADO')).toBeInTheDocument();
  });

  it('redirige al dashboard del rol cuando el rol no está permitido', () => {
    estado.user = { usuarioId: 2, rol: 'ROLE_ASESOR', correo: 'asesor@turnos.local' };
    estado.isAuthenticated = true;
    render(
      <MemoryRouter initialEntries={['/privado']}>
        <Routes>
          <Route path="/asesor" element={<div>PANTALLA PROPIA</div>} />
          <Route
            path="/privado"
            element={
              <ProtectedRoute roles={['ROLE_ADMINISTRADOR']}>
                <div>CONTENIDO PRIVADO</div>
              </ProtectedRoute>
            }
          />
        </Routes>
      </MemoryRouter>
    );
    expect(screen.getByText('PANTALLA PROPIA')).toBeInTheDocument();
    expect(screen.queryByText('CONTENIDO PRIVADO')).not.toBeInTheDocument();
  });
});
