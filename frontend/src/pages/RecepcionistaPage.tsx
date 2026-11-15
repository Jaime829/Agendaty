import { useState, useEffect, useCallback } from 'react';
import { toast } from 'sonner';
import { turnosService, tiposTramiteService } from '@/api';
import {
  BadgeEstado,
  BadgePrioridad,
  Card,
  CardHeader,
  Button,
  Input,
  Select,
  Alert,
  Skeleton,
  EmptyState,
  ConfirmDialog,
} from '@/components';
import { useRolGuard } from '@/hooks/useRolGuard';
import type { TipoTramite } from '@/types';

export default function RecepcionistaPage() {
  const { esPermitido, user } = useRolGuard(['ROLE_RECEPCIONISTA', 'ROLE_ADMINISTRADOR']);

  const [tipos, setTipos] = useState<TipoTramite[]>([]);
  const [turnos, setTurnos] = useState<any[]>([]);
  const [form, setForm] = useState({ ciudadanoId: '', tipoTramiteId: '' });
  const [prioritario, setPrioritario] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [generando, setGenerando] = useState(false);
  const [cargandoTurnos, setCargandoTurnos] = useState(true);
  const [turnoACancelar, setTurnoACancelar] = useState<any>(null);
  const [cancelando, setCancelando] = useState(false);

  const cargarTurnos = useCallback(async () => {
    setCargandoTurnos(true);
    try {
      const data = await turnosService.listar();
      setTurnos(data ?? []);
    } catch (e: any) {
      setError(e.message || 'No se pudo cargar la cola de turnos');
    } finally {
      setCargandoTurnos(false);
    }
  }, []);

  useEffect(() => {
    tiposTramiteService
      .listar()
      .then(setTipos)
      .catch((e: any) => setError(e.message || 'No se pudo cargar los tipos de trámite'));
    cargarTurnos();
  }, [cargarTurnos]);

  const generarTurno = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;
    setError(null);
    setFieldErrors({});
    setGenerando(true);
    try {
      const creado = await turnosService.generar({
        usuarioId: user.usuarioId,
        turno: {
          ciudadanoId: Number(form.ciudadanoId),
          tipoTramiteId: Number(form.tipoTramiteId),
          prioritario,
        },
      });
      toast.success(`Turno ${creado.numeroTurno ?? ''} generado`);
      setForm({ ciudadanoId: '', tipoTramiteId: '' });
      setPrioritario(false);
      cargarTurnos();
    } catch (e: any) {
      setError(e.message || 'No se pudo generar el turno');
      setFieldErrors(e.fieldErrors || {});
    } finally {
      setGenerando(false);
    }
  };

  const confirmarCancelacion = async () => {
    if (!turnoACancelar || !user) return;
    setCancelando(true);
    try {
      await turnosService.cancelar(turnoACancelar.id, Number(user.usuarioId));
      toast.success(`Turno ${turnoACancelar.numeroTurno} cancelado`);
      setTurnoACancelar(null);
      cargarTurnos();
    } catch (e: any) {
      setError(e.message || 'No se pudo cancelar el turno');
    } finally {
      setCancelando(false);
    }
  };

  if (!esPermitido) return null;

  const enEspera = turnos.filter((t) => t.estado === 'EN_ESPERA');
  const enAtencion = turnos.filter((t) => t.estado === 'EN_ATENCION' || t.estado === 'EN_PAUSA' || t.estado === 'LLAMADO');

  return (
    <div className="page-gutter mx-auto max-w-7xl pt-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Recepción</h1>
        <p className="text-sm text-slate-500">Registra el trámite del ciudadano y entrega su número de turno.</p>
      </div>

      {error && (
        <div className="mb-4">
          <Alert tipo="error" mensaje={error} fieldErrors={fieldErrors} onCerrar={() => { setError(null); setFieldErrors({}); }} />
        </div>
      )}

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        {/* Generar turno */}
        <Card className="p-6 lg:col-span-2">
          <CardHeader
            titulo="Generar turno"
            descripcion="Selecciona el tipo de trámite y la prioridad."
          />
          <form onSubmit={generarTurno} className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <Input
              label="ID del ciudadano *"
              placeholder="Ej. 5"
              inputMode="numeric"
              pattern="[0-9]*"
              value={form.ciudadanoId}
              onChange={(e) => setForm({ ...form, ciudadanoId: e.target.value })}
              required
            />
            <Select
              label="Tipo de trámite *"
              options={tipos.map((t) => ({ value: String(t.id), label: `${t.codigo} — ${t.nombre}` }))}
              value={form.tipoTramiteId}
              onChange={(e) => setForm({ ...form, tipoTramiteId: e.target.value })}
              required
            />
            <fieldset className="md:col-span-2">
              <legend className="mb-1 text-sm font-medium text-slate-700">Prioridad</legend>
              <div className="flex gap-3">
                {[
                  { valor: false, etiqueta: 'Normal' },
                  { valor: true, etiqueta: 'Prioritario (RN/CA4)' },
                ].map(({ valor, etiqueta }) => (
                  <label
                    key={String(valor)}
                    className={`flex h-11 cursor-pointer items-center gap-2 rounded-md border px-4 text-sm font-medium transition-colors ${
                      prioritario === valor
                        ? 'border-brand-600 bg-brand-50 text-brand-800 ring-2 ring-brand-600'
                        : 'border-slate-300 text-slate-600 hover:bg-slate-50'
                    }`}
                  >
                    <input
                      type="radio"
                      name="prioridad"
                      className="sr-only"
                      checked={prioritario === valor}
                      onChange={() => setPrioritario(valor)}
                    />
                    {etiqueta}
                  </label>
                ))}
              </div>
            </fieldset>
            <Button type="submit" size="lg" className="md:col-span-2" loading={generando}>
              Generar turno
            </Button>
          </form>
        </Card>

        {/* Cola */}
        <Card className="p-6">
          <CardHeader
            titulo="Cola de turnos"
            descripcion={`${enEspera.length} en espera · ${enAtencion.length} en atención`}
            accion={
              <Button variant="ghost" size="sm" onClick={cargarTurnos} aria-label="Actualizar cola">
                Actualizar
              </Button>
            }
          />
          {cargandoTurnos ? (
            <div className="space-y-3">
              {[0, 1, 2].map((i) => (
                <Skeleton key={i} className="h-12 w-full" />
              ))}
            </div>
          ) : turnos.length === 0 ? (
            <EmptyState
              titulo="La cola está vacía"
              descripcion="Genera el primer turno del día para comenzar la atención."
            />
          ) : (
            <div className="-mx-2 overflow-x-auto px-2">
              <table className="table-data">
                <thead>
                  <tr>
                    <th scope="col">Número</th>
                    <th scope="col">Trámite</th>
                    <th scope="col">Estado</th>
                    <th scope="col">Prioridad</th>
                    <th scope="col" className="text-right">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {turnos.slice(0, 12).map((t) => (
                    <tr key={t.id}>
                      <td className="font-mono font-semibold text-brand-800">{t.numeroTurno}</td>
                      <td className="text-slate-600">{t.tipoTramiteNombre}</td>
                      <td>
                        <BadgeEstado estado={t.estado} />
                      </td>
                      <td>
                        <BadgePrioridad prioritario={t.prioridad === 'PRIORITARIO'} />
                      </td>
                      <td className="text-right">
                        {t.estado === 'EN_ESPERA' && (
                          <Button variant="destructive" size="sm" onClick={() => setTurnoACancelar(t)}>
                            Cancelar
                          </Button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </div>

      {turnoACancelar && (
        <ConfirmDialog
          titulo="Cancelar turno"
          mensaje={`¿Seguro que deseas cancelar el turno ${turnoACancelar.numeroTurno}? Esta acción no se puede deshacer.`}
          confirmarTexto="Sí, cancelar turno"
          cargando={cancelando}
          onConfirmar={confirmarCancelacion}
          onCancelar={() => setTurnoACancelar(null)}
        />
      )}
    </div>
  );
}
