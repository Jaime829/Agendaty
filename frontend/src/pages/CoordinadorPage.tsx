import { useState, useEffect, useCallback, useRef } from 'react';
import { turnosService, reportesService } from '@/api';
import {
  BadgeEstado,
  BadgePrioridad,
  Card,
  CardHeader,
  Button,
  Alert,
  Skeleton,
  EmptyState,
} from '@/components';
import { useRolGuard } from '@/hooks/useRolGuard';

const INTERVALO_MS = 5000;

export default function CoordinadorPage() {
  const { esPermitido, loading: sesionLoading } = useRolGuard(['ROLE_COORDINADOR', 'ROLE_ADMINISTRADOR']);

  const [turnos, setTurnos] = useState<any[]>([]);
  const [reporte, setReporte] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [cargandoInicial, setCargandoInicial] = useState(true);
  const [ultimaActualizacion, setUltimaActualizacion] = useState<Date | null>(null);
  const montado = useRef(true);

  const cargarTodo = useCallback(async (silencioso = true) => {
    if (!silencioso) setCargandoInicial(true);
    try {
      const [t, r] = await Promise.all([
        turnosService.listar(),
        reportesService.tiempoEsperaPromedio(),
      ]);
      if (!montado.current) return;
      setTurnos(t ?? []);
      setReporte(r);
      setUltimaActualizacion(new Date());
      setError(null);
    } catch (e: any) {
      if (montado.current) setError(e.message || 'No se pudo actualizar el monitor');
    } finally {
      if (montado.current) setCargandoInicial(false);
    }
  }, []);

  useEffect(() => {
    montado.current = true;
    if (sesionLoading) return;
    const cargar = () =>
      // Inline (no llamar a cargarTodo): la regla react-hooks/set-state-in-effect
      // no admite invocar desde el efecto funciones que llaman setState.
      Promise.all([turnosService.listar(), reportesService.tiempoEsperaPromedio()])
        .then(([t, r]) => {
          if (!montado.current) return;
          setTurnos(t ?? []);
          setReporte(r);
          setUltimaActualizacion(new Date());
          setError(null);
        })
        .catch((e: any) => {
          if (montado.current) setError(e.message || 'No se pudo actualizar el monitor');
        })
        .finally(() => {
          if (montado.current) setCargandoInicial(false);
        });
    cargar();
    const id = setInterval(cargar, INTERVALO_MS);
    return () => {
      montado.current = false;
      clearInterval(id);
    };
  }, [sesionLoading]);

  if (!esPermitido) return null;

  const enEspera = turnos.filter((t) => t.estado === 'EN_ESPERA');
  const enAtencion = turnos.filter((t) => t.estado === 'EN_ATENCION' || t.estado === 'EN_PAUSA' || t.estado === 'LLAMADO');
  const enPausa = turnos.filter((t) => t.estado === 'EN_PAUSA');

  const stats = [
    { etiqueta: 'En espera', valor: enEspera.length, tono: 'text-amber-700 bg-amber-50 ring-amber-200' },
    { etiqueta: 'En atención', valor: enAtencion.length, tono: 'text-indigo-700 bg-indigo-50 ring-indigo-200' },
    { etiqueta: 'En pausa', valor: enPausa.length, tono: 'text-slate-700 bg-slate-100 ring-slate-200' },
    {
      etiqueta: 'Espera promedio',
      valor: reporte?.valor != null ? `${reporte.valor} min` : '—',
      tono: 'text-brand-800 bg-brand-50 ring-brand-200',
    },
  ];

  return (
    <div className="page-gutter mx-auto max-w-7xl pt-6">
      <div className="mb-6 flex flex-wrap items-end justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Monitor de atención</h1>
          <p className="text-sm text-slate-500">
            Vista en vivo de la cola{' '}
            {ultimaActualizacion && (
              <span className="text-slate-400">
                · actualizado {ultimaActualizacion.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit', second: '2-digit' })}
              </span>
            )}
          </p>
        </div>
        <Button variant="secondary" size="sm" onClick={() => cargarTodo(false)} aria-label="Actualizar monitor">
          Actualizar
        </Button>
      </div>

      {error && (
        <div className="mb-4">
          <Alert tipo="error" mensaje={error} onCerrar={() => setError(null)} />
        </div>
      )}

      {cargandoInicial ? (
        <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
          {[0, 1, 2, 3].map((i) => (
            <Skeleton key={i} className="h-24 w-full rounded-xl" />
          ))}
        </div>
      ) : (
        <dl className="grid grid-cols-2 gap-4 lg:grid-cols-4" aria-live="polite">
          {stats.map(({ etiqueta, valor, tono }) => (
            <div key={etiqueta} className={`rounded-xl p-4 ring-1 ${tono}`}>
              <dt className="text-xs font-semibold uppercase tracking-wide opacity-80">{etiqueta}</dt>
              <dd className="mt-1 text-3xl font-bold tabular-nums">{valor}</dd>
            </div>
          ))}
        </dl>
      )}

      <Card className="mt-6 p-6">
        <CardHeader
          titulo="Cola de turnos"
          descripcion="Ordenada como en pantalla de atención. Prioritarios primero."
        />
        {cargandoInicial ? (
          <div className="space-y-3">
            {[0, 1, 2, 3].map((i) => (
              <Skeleton key={i} className="h-12 w-full" />
            ))}
          </div>
        ) : turnos.length === 0 ? (
          <EmptyState titulo="Sin turnos hoy" descripcion="Genera un turno desde recepción para verlos aquí." />
        ) : (
          <div className="-mx-2 overflow-x-auto px-2">
            <table className="table-data">
              <thead>
                <tr>
                  <th scope="col">Número</th>
                  <th scope="col">Trámite</th>
                  <th scope="col">Estado</th>
                  <th scope="col">Prioridad</th>
                  <th scope="col">Asesor</th>
                </tr>
              </thead>
              <tbody>
                {[...turnos]
                  .sort((a, b) => {
                    if (a.prioridad !== b.prioridad) return a.prioridad === 'PRIORITARIO' ? -1 : 1;
                    return String(a.fechaCreacion).localeCompare(String(b.fechaCreacion));
                  })
                  .slice(0, 20)
                  .map((t) => (
                    <tr key={t.id}>
                      <td className="font-mono font-semibold text-brand-800">{t.numeroTurno}</td>
                      <td className="text-slate-600">{t.tipoTramiteNombre ?? '—'}</td>
                      <td>
                        <BadgeEstado estado={t.estado} />
                      </td>
                      <td>
                        <BadgePrioridad prioritario={t.prioridad === 'PRIORITARIO'} />
                      </td>
                      <td className="text-slate-600">{t.asesorNombre ?? '—'}</td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  );
}
