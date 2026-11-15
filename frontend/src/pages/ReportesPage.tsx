import { useState } from 'react';
import { reportesService } from '@/api';
import {
  Card,
  CardHeader,
  Button,
  Input,
  Alert,
  Skeleton,
  EmptyState,
} from '@/components';
import { useRolGuard } from '@/hooks/useRolGuard';

export default function ReportesPage() {
  const { esPermitido } = useRolGuard(['ROLE_COORDINADOR', 'ROLE_ADMINISTRADOR']);

  const [fechaDesde, setFechaDesde] = useState('');
  const [fechaHasta, setFechaHasta] = useState('');
  const [promedio, setPromedio] = useState<any>(null);
  const [atendidos, setAtendidos] = useState<any>(null);
  const [tramites, setTramites] = useState<any[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [cargando, setCargando] = useState(false);

  const rangoInvalido = fechaDesde && fechaHasta && fechaDesde > fechaHasta;

  const consultar = async () => {
    if (rangoInvalido) {
      setError('La fecha "desde" no puede ser posterior a "hasta"');
      return;
    }
    setError(null);
    setCargando(true);
    const filtros = {
      ...(fechaDesde ? { fechaDesde } : {}),
      ...(fechaHasta ? { fechaHasta } : {}),
    };
    try {
      const [p, a, t] = await Promise.all([
        reportesService.tiempoEsperaPromedio(filtros),
        reportesService.turnosAtendidos(filtros),
        reportesService.tramitesFrecuentes(filtros),
      ]);
      setPromedio(p);
      setAtendidos(a);
      setTramites(t ?? []);
    } catch (e: any) {
      setError(e.message || 'No se pudo cargar el reporte');
    } finally {
      setCargando(false);
    }
  };

  if (!esPermitido) return null;

  const maxTramite = (tramites ?? []).reduce((m, t) => Math.max(m, Number(t.cantidad) || 0), 0) || 1;
  const atendidosTotal = atendidos?.atendidos ?? 0;
  const noAtendidosTotal = atendidos?.no_atendidos ?? atendidos?.noAtendidos ?? 0;
  const totalAtendidos = Number(atendidosTotal);
  const totalNoAtendidos = Number(noAtendidosTotal);
  const total = totalAtendidos + totalNoAtendidos;
  const tasaAtencion = total > 0 ? Math.round((totalAtendidos / total) * 100) : null;

  return (
    <div className="page-gutter mx-auto max-w-7xl pt-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Reportes</h1>
        <p className="text-sm text-slate-500">Tiempos de espera, turnos atendidos y trámites frecuentes.</p>
      </div>

      {error && (
        <div className="mb-4">
          <Alert tipo="error" mensaje={error} onCerrar={() => setError(null)} />
        </div>
      )}

      {/* Filtros */}
      <Card className="mb-6 p-6">
        <div className="grid grid-cols-1 gap-4 md:grid-cols-[1fr_1fr_auto] md:items-end">
          <Input
            label="Desde"
            type="date"
            value={fechaDesde}
            onChange={(e) => setFechaDesde(e.target.value)}
          />
          <Input
            label="Hasta"
            type="date"
            value={fechaHasta}
            onChange={(e) => setFechaHasta(e.target.value)}
            hint={rangoInvalido ? undefined : 'Deja vacío para ver todo el historial'}
          />
          <Button size="lg" onClick={consultar} loading={cargando} disabled={!!rangoInvalido}>
            Consultar
          </Button>
        </div>
      </Card>

      {cargando ? (
        <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
          <Skeleton className="h-40 w-full rounded-xl" />
          <Skeleton className="h-40 w-full rounded-xl" />
          <Skeleton className="h-56 w-full rounded-xl md:col-span-2" />
        </div>
      ) : (
        <>
          {/* Indicadores */}
          <div className="grid grid-cols-2 gap-4 md:grid-cols-4" aria-live="polite">
            {[
              { etiqueta: 'Espera promedio', valor: promedio?.valor != null ? `${promedio.valor} min` : '—' },
              { etiqueta: 'Atendidos', valor: atendidos?.atendidos ?? '—' },
              { etiqueta: 'No atendidos', valor: atendidos?.no_atendidos ?? atendidos?.noAtendidos ?? '—' },
              { etiqueta: 'Tasa de atención', valor: tasaAtencion != null ? `${tasaAtencion}%` : '—' },
            ].map(({ etiqueta, valor }) => (
              <div key={etiqueta} className="rounded-xl border border-slate-200 bg-white p-4 shadow-card">
                <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">{etiqueta}</p>
                <p className="mt-1 text-3xl font-bold tabular-nums text-brand-800">{valor}</p>
              </div>
            ))}
          </div>

          {/* Trámites frecuentes */}
          <Card className="mt-6 p-6">
            <CardHeader
              titulo="Trámites más frecuentes"
              descripcion="Cuántas veces se solicitó cada tipo de trámite en el período."
            />
            {!tramites ? (
              <p className="text-sm text-slate-500">Consulta un período para ver resultados.</p>
            ) : tramites.length === 0 ? (
              <EmptyState
                titulo="Sin datos en el período"
                descripcion="Aún no hay atenciones registradas entre las fechas elegidas."
              />
            ) : (
              <ol className="space-y-3">
                {[...tramites]
                  .sort((a, b) => Number(b.cantidad) - Number(a.cantidad))
                  .map((t, i) => {
                    const nombre = t.tipo_tramite_nombre ?? t.tipoTramite ?? 'Sin nombre';
                    return (
                    <li key={`${nombre}-${i}`} className="flex items-center gap-4">
                      <span className="w-48 shrink-0 truncate text-sm text-slate-600" title={nombre}>
                        {nombre}
                      </span>
                      <div className="h-6 flex-1 overflow-hidden rounded-md bg-slate-100">
                        <div
                          className="h-full rounded-md bg-brand-500"
                          style={{ width: `${Math.max(4, (Number(t.cantidad) / maxTramite) * 100)}%` }}
                        />
                      </div>
                      <span className="w-12 text-right text-sm font-semibold tabular-nums text-slate-900">{t.cantidad}</span>
                    </li>
                    );
                  })}
              </ol>
            )}
          </Card>
        </>
      )}
    </div>
  );
}
