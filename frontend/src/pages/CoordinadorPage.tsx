import { useState, useEffect, useCallback, useRef } from 'react';
import { turnosService, reportesService, asesoresService } from '@/api';
import {
  BadgeEstado,
  BadgePrioridad,
  Card,
  CardHeader,
  Button,
  Alert,
  PageHeader,
  KpiCard,
  DataTable,
  Skeleton,
  EmptyState,
} from '@/components';
import { useRolGuard } from '@/hooks/useRolGuard';

const INTERVALO_MS = 5000;

export default function CoordinadorPage() {
  const { esPermitido, loading: sesionLoading } = useRolGuard(['ROLE_COORDINADOR', 'ROLE_ADMINISTRADOR']);

  const [turnos, setTurnos] = useState<any[]>([]);
  const [reporte, setReporte] = useState<any>(null);
  const [asesores, setAsesores] = useState<any[]>([]);
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
    } catch {
      if (montado.current) setError('No se pudo actualizar el monitor. Revisa que el backend esté disponible.');
    } finally {
      if (montado.current) setCargandoInicial(false);
    }
  }, []);

  // Asesores en un efecto separado: GET /asesores puede fallar (el backend lanza NPE
  // si un asesor no tiene ventanilla asignada); degradación graceful del panel.
  useEffect(() => {
    if (sesionLoading) return;
    let activo = true;
    asesoresService
      .listar()
      .then((a) => { if (activo) setAsesores(a ?? []); })
      .catch(() => { if (activo) setAsesores([]); });
    return () => { activo = false; };
  }, [sesionLoading]);

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

  // Carga por asesor: turnos activos asignados a cada asesor ahora mismo
  const cargaPorAsesor = asesores.map((a: any) => ({
    ...a,
    turnosActivos: turnos.filter(
      (t: any) => t.asesorId === a.id && ['LLAMADO', 'EN_ATENCION', 'EN_PAUSA'].includes(t.estado)
    ).length,
  }));

  const stats = [
    { etiqueta: 'En espera', valor: enEspera.length },
    { etiqueta: 'En atención', valor: enAtencion.length },
    { etiqueta: 'En pausa', valor: enPausa.length },
    { etiqueta: 'Espera promedio', valor: reporte?.valor != null ? `${reporte.valor} min` : '—' },
  ];

  return (
    <div className="page-gutter mx-auto max-w-7xl pt-6">
      <PageHeader
        titulo="Monitor de atención"
        descripcion={
          ultimaActualizacion
            ? `Vista en vivo · actualizado ${ultimaActualizacion.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit', second: '2-digit' })}`
            : 'Vista en vivo de la cola'
        }
        accion={<Button variant="secondary" size="sm" onClick={() => cargarTodo(true)}>Actualizar</Button>}
      />

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
        <div className="grid grid-cols-2 gap-3 lg:grid-cols-4" aria-live="polite" aria-label="Indicadores de cola">
          {stats.map(({ etiqueta, valor }) => (
            <KpiCard key={etiqueta} etiqueta={etiqueta} valor={valor} />
          ))}
        </div>
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
          <DataTable
            columns={[
              { key: 'numeroTurno', header: 'Número', sortValue: (t: any) => t.numeroTurno },
              { key: 'tipoTramiteNombre', header: 'Trámite', sortValue: (t: any) => (t.tipoTramiteNombre ?? '').toLowerCase() },
              { key: 'estado', header: 'Estado', render: (t: any) => <BadgeEstado estado={t.estado} />, sortValue: (t: any) => t.estado },
              { key: 'prioridad', header: 'Prioridad', render: (t: any) => <BadgePrioridad prioritario={t.prioridad === 'PRIORITARIO'} />, sortValue: (t: any) => (t.prioridad === 'PRIORITARIO' ? 0 : 1) },
              { key: 'asesorNombre', header: 'Asesor', render: (t: any) => t.asesorNombre ?? '—', sortValue: (t: any) => t.asesorNombre ?? '' },
            ]}
            rows={[...turnos]
              .sort((a: any, b: any) => {
                if (a.prioridad !== b.prioridad) return a.prioridad === 'PRIORITARIO' ? -1 : 1;
                return String(a.fechaCreacion).localeCompare(String(b.fechaCreacion));
              })}
            rowKey={(t: any) => t.id}
            searchText={(t: any) => `${t.numeroTurno} ${t.tipoTramiteNombre ?? ''} ${t.asesorNombre ?? ''}`}
            searchPlaceholder="Buscar en la cola…"
            pageSize={10}
            caption="Cola de turnos actual"
          />
        )}
      </Card>

      {/* Carga por asesor */}
      {!cargandoInicial && cargaPorAsesor.length > 0 && (
        <Card className="mt-6 p-6">
          <CardHeader
            titulo="Carga por asesor"
            descripcion="Turnos activos asignados a cada asesor ahora mismo."
          />
          <ul className="space-y-2">
            {cargaPorAsesor.map((a: any) => (
              <li key={a.id} className="flex items-center justify-between gap-4 rounded-lg border border-slate-100 bg-slate-50 px-4 py-2.5">
                <span className="text-sm font-medium text-slate-800">{a.nombre}</span>
                <span className="flex items-center gap-2">
                  {a.turnosActivos > 0 ? (
                    <span className="inline-flex h-6 min-w-6 items-center justify-center rounded-full bg-indigo-600 px-2 text-xs font-bold tabular-nums text-white">
                      {a.turnosActivos}
                    </span>
                  ) : (
                    <span className="text-xs text-slate-400">sin turnos activos</span>
                  )}
                  <span className={`h-2.5 w-2.5 rounded-full ${a.disponible ? 'bg-green-500' : 'bg-slate-300'}`} aria-hidden="true" />
                </span>
              </li>
            ))}
          </ul>
        </Card>
      )}
    </div>
  );
}
