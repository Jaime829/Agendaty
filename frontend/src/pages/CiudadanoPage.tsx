import { useState } from 'react';
import { turnosService } from '@/api';
import {
  BadgeEstado,
  BadgePrioridad,
  Card,
  Button,
  Input,
  Alert,
  EmptyState,
  PageHeader,
} from '@/components';
import { useRolGuard } from '@/hooks/useRolGuard';

export default function CiudadanoPage() {
  const { esPermitido } = useRolGuard(['ROLE_CIUDADANO', 'ROLE_ADMINISTRADOR']);

  const [numero, setNumero] = useState('');
  const [turno, setTurno] = useState<any>(null);
  const [busque, setBusque] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [cargando, setCargando] = useState(false);

  const consultar = async () => {
    const q = numero.trim().toUpperCase();
    if (!q) {
      setError('Ingresa tu número de turno');
      return;
    }
    setError(null);
    setCargando(true);
    setBusque(true);
    try {
      const turnos = await turnosService.listar();
      const t = (turnos ?? []).find((x: any) => x.numeroTurno === q) ?? null;
      setTurno(t);
      if (!t) setError('No encontramos un turno con ese número. Verifícalo e intenta de nuevo.');
    } catch (e: any) {
      setError(e.message || 'No se pudo consultar el turno');
      setTurno(null);
    } finally {
      setCargando(false);
    }
  };

  if (!esPermitido) return null;

  return (
    <div className="page-gutter mx-auto max-w-3xl pt-8">
      <PageHeader
        titulo="Mi turno"
        descripcion="Ingresa tu número y conoce tu estado en la cola."
      />

      <Card className="mx-auto max-w-md p-6">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            consultar();
          }}
          className="flex flex-col gap-3 sm:flex-row sm:items-end"
        >
          <div className="flex-1">
            <Input
              id="numero-turno"
              label="Número de turno"
              placeholder="Ej. CEDN-00001"
              autoCapitalize="characters"
              autoComplete="off"
              spellCheck={false}
              value={numero}
              onChange={(e) => setNumero(e.target.value)}
              required
            />
          </div>
          <Button type="submit" size="lg" loading={cargando} className="sm:mb-[1.05rem]">
            Consultar
          </Button>
        </form>
        {error && (
          <div className="mt-3" role="alert">
            <Alert tipo="error" mensaje={error} onCerrar={() => setError(null)} />
          </div>
        )}
      </Card>

      {turno && (
        <Card className="mx-auto mt-6 max-w-md p-6 text-center" aria-live="polite">
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Tu turno</p>
          <p className="mt-1 font-mono text-4xl font-bold tracking-tight text-brand-800">{turno.numeroTurno}</p>
          <div className="mt-4 flex items-center justify-center gap-2">
            <BadgeEstado estado={turno.estado} />
            <BadgePrioridad prioritario={turno.prioridad === 'PRIORITARIO'} />
          </div>
          <dl className="mt-6 divide-y divide-slate-100 text-left text-sm">
            {turno.tipoTramiteNombre && (
              <div className="flex items-baseline justify-between gap-4 py-3">
                <dt className="text-slate-500">Trámite</dt>
                <dd className="font-semibold text-slate-900">{turno.tipoTramiteNombre}</dd>
              </div>
            )}
            {turno.fechaLlamado && (
              <div className="flex items-baseline justify-between gap-4 py-3">
                <dt className="text-slate-500">Llamado a las</dt>
                <dd className="font-semibold text-slate-900">
                  {new Date(turno.fechaLlamado).toLocaleString('es-CO', {
                    hour: '2-digit',
                    minute: '2-digit',
                    day: '2-digit',
                    month: 'short',
                  })}
                </dd>
              </div>
            )}
          </dl>
          <p className="mt-4 text-xs text-slate-400">Muestra este número en pantalla cuando te llamen.</p>
        </Card>
      )}

      {busque && !turno && !cargando && !error && (
        <div className="mx-auto mt-6 max-w-md">
          <EmptyState
            titulo="Turno no encontrado"
            descripcion="Verifica el número impreso en tu comprobante y consulta de nuevo."
          />
        </div>
      )}
    </div>
  );
}
