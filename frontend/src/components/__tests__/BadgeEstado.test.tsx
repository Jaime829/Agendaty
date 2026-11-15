import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BadgeEstado, BadgePrioridad } from '@/components/BadgeEstado';

describe('BadgeEstado', () => {
  it('muestra etiqueta legible en español para cada estado conocido', () => {
    const casos: Array<[string, string]> = [
      ['EN_ESPERA', 'En espera'],
      ['LLAMADO', 'Llamado'],
      ['EN_ATENCION', 'En atención'],
      ['EN_PAUSA', 'En pausa'],
      ['FINALIZADO', 'Finalizado'],
      ['CANCELADO', 'Cancelado'],
      ['NO_ATENDIDO', 'No atendido'],
    ];
    for (const [estado, etiqueta] of casos) {
      const { unmount } = render(<BadgeEstado estado={estado} />);
      expect(screen.getByText(etiqueta)).toBeInTheDocument();
      unmount();
    }
  });

  it('expone el estado como accessible name (aria-label)', () => {
    render(<BadgeEstado estado="EN_ATENCION" />);
    expect(screen.getByLabelText('Estado: En atención')).toBeInTheDocument();
  });

  it('renderiza el estado desconocido sin romperse', () => {
    render(<BadgeEstado estado="OLE" />);
    expect(screen.getByText('OLE')).toBeInTheDocument();
  });
});

describe('BadgePrioridad', () => {
  it('muestra Prioritario cuando aplica', () => {
    render(<BadgePrioridad prioritario />);
    expect(screen.getByText('Prioritario')).toBeInTheDocument();
  });

  it('no muestra badge de prioridad para turnos normales', () => {
    render(<BadgePrioridad prioritario={false} />);
    expect(screen.queryByText('Prioritario')).not.toBeInTheDocument();
    expect(screen.getByText('Normal')).toBeInTheDocument();
  });
});
