import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { PublicNav } from '@/components/PublicNav';

const ctx = vi.hoisted(() => ({
  state: {
    user: null as any,
    loading: false,
    isAuthenticated: false,
    login: vi.fn(),
    logout: vi.fn(),
    tieneRol: () => false,
    tieneAlgunaRol: () => false,
  },
}));

vi.mock('@/context/AuthContext', () => ({
  useAuthContext: () => ctx.state,
}));

function renderNav() {
  return render(
    <MemoryRouter initialEntries={['/']}>
      <Routes>
        <Route path="/" element={<PublicNav />} />
        <Route path="/ciudadano" element={<div>PANEL CIUDADANO</div>} />
        <Route path="/recepcionista" element={<div>PANEL RECEPCIONISTA</div>} />
        <Route path="/asesor" element={<div>PANEL ASESOR</div>} />
        <Route path="/coordinador" element={<div>PANEL COORDINADOR</div>} />
        <Route path="/administrador" element={<div>PANEL ADMINISTRADOR</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe('PublicNav', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    ctx.state.user = null;
    ctx.state.loading = false;
    ctx.state.isAuthenticated = false;
  });

  it('sin sesión muestra Iniciar sesión y Crear cuenta, y no Ir a mi panel', () => {
    renderNav();
    expect(screen.getByText('Iniciar sesión')).toBeInTheDocument();
    expect(screen.getByText('Crear cuenta')).toBeInTheDocument();
    expect(screen.queryByText('Ir a mi panel')).not.toBeInTheDocument();
  });

  it('mientras carga la sesión no muestra botones de login ni de panel', () => {
    ctx.state.loading = true;
    renderNav();
    expect(screen.queryByText('Iniciar sesión')).not.toBeInTheDocument();
    expect(screen.queryByText('Crear cuenta')).not.toBeInTheDocument();
    expect(screen.queryByText('Ir a mi panel')).not.toBeInTheDocument();
  });

  it.each([
    ['ROLE_CIUDADANO', '/ciudadano'],
    ['ROLE_RECEPCIONISTA', '/recepcionista'],
    ['ROLE_ASESOR', '/asesor'],
    ['ROLE_COORDINADOR', '/coordinador'],
    ['ROLE_ADMINISTRADOR', '/administrador'],
  ])('con sesión de rol %s muestra Ir a mi panel hacia %s', async (rol, ruta) => {
    ctx.state.user = { usuarioId: 1, username: 'persona@email.com', rol };
    ctx.state.isAuthenticated = true;
    renderNav();
    const enlace = screen.getByRole('link', { name: 'Ir a mi panel' });
    expect(enlace).toHaveAttribute('href', ruta);
    expect(screen.queryByText('Iniciar sesión')).not.toBeInTheDocument();
    expect(screen.queryByText('Crear cuenta')).not.toBeInTheDocument();
  });

  it('con sesión el menú de usuario permite cerrar sesión', async () => {
    const userEvent = (await import('@testing-library/user-event')).default;
    const user = userEvent.setup();
    ctx.state.user = { usuarioId: 1, username: 'persona@email.com', rol: 'ROLE_ASESOR' };
    ctx.state.isAuthenticated = true;
    renderNav();
    await user.click(screen.getByRole('button', { name: /abrir menú de usuario/i }));
    const boton = await screen.findByRole('menuitem', { name: /cerrar sesión/i });
    await user.click(boton);
    expect(ctx.state.logout).toHaveBeenCalledTimes(1);
  });
});
