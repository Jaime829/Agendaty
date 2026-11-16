import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import RecepcionistaPage from '@/pages/RecepcionistaPage';

const apiMock = vi.hoisted(() => ({
  listarTurnos: vi.fn(),
  generarTurno: vi.fn(),
  cancelarTurno: vi.fn(),
  listarTipos: vi.fn(),
}));

vi.mock('@/hooks/useRolGuard', () => ({
  useRolGuard: () => ({
    esPermitido: true,
    user: { usuarioId: 5, correo: 'recepcion@turnos.local', rol: 'ROLE_RECEPCIONISTA' },
    loading: false,
  }),
}));

vi.mock('@/api', () => ({
  turnosService: {
    listar: apiMock.listarTurnos,
    generar: apiMock.generarTurno,
    cancelar: apiMock.cancelarTurno,
    llamarSiguiente: vi.fn(),
    obtener: vi.fn(),
    marcarNoAtendido: vi.fn(),
  },
  tiposTramiteService: {
    listar: apiMock.listarTipos,
  },
  reportesService: {
    tiempoEsperaPromedio: vi.fn().mockResolvedValue({ valor: 0, cantidad_atenciones: 0 }),
  },
}));

describe('RecepcionistaPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    apiMock.listarTipos.mockResolvedValue([
      { id: 1, codigo: 'CED', nombre: 'Cédula de identidad', esPrioritarioPorDefecto: false, activo: true },
    ]);
    apiMock.listarTurnos.mockResolvedValue([]);
    apiMock.generarTurno.mockResolvedValue({ id: 10, numeroTurno: 'CEDN-00099', estado: 'EN_ESPERA' });
  });

  it('carga los tipos de trámite al montar', async () => {
    render(
      <MemoryRouter>
        <RecepcionistaPage />
      </MemoryRouter>
    );
    await waitFor(() => expect(apiMock.listarTipos).toHaveBeenCalled());
    expect(await screen.findByLabelText(/Tipo de trámite/i)).toBeInTheDocument();
  });

  it('generar turno envía ciudadanoId, tipoTramiteId y prioridad al servicio', async () => {
    const user = userEvent.setup();
    render(
      <MemoryRouter>
        <RecepcionistaPage />
      </MemoryRouter>
    );

    await user.type(screen.getByLabelText(/ID del ciudadano/i), '5');
    await user.selectOptions(await screen.findByLabelText(/Tipo de trámite/i), '1');
    await user.click(screen.getByRole('button', { name: /generar turno/i }));

    await waitFor(() => expect(apiMock.generarTurno).toHaveBeenCalled());
    expect(apiMock.generarTurno).toHaveBeenCalledWith({
      usuarioId: 5,
      turno: { ciudadanoId: 5, tipoTramiteId: 1, prioritario: false },
    });
  });

  it('muestra el estado vacío cuando no hay turnos', async () => {
    render(
      <MemoryRouter>
        <RecepcionistaPage />
      </MemoryRouter>
    );
    expect(await screen.findByText('La cola está vacía')).toBeInTheDocument();
  });
});
