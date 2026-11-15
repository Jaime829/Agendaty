import { useState, useEffect, useCallback } from 'react';
import { toast } from 'sonner';
import { turnosService, atencionesService } from '@/api';
import {
  BadgeEstado,
  BadgePrioridad,
  Card,
  CardHeader,
  Button,
  Input,
  Alert,
  Skeleton,
} from '@/components';
import { useRolGuard } from '@/hooks/useRolGuard';

const ESTADOS_ACTIVOS = ['LLAMADO', 'EN_ATENCION', 'EN_PAUSA'];

export default function AsesorPage() {
  const { esPermitido, user, loading: sesionLoading } = useRolGuard(['ROLE_ASESOR', 'ROLE_ADMINISTRADOR']);

  const [turno, setTurno] = useState<any>(null);
  const [atencion, setAtencion] = useState<any>(null);
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [accionEnCurso, setAccionEnCurso] = useState<string | null>(null);
  const [resultado, setResultado] = useState('');
  const [observaciones, setObservaciones] = useState('');

  const estado = turno?.estado as string | undefined;
  const enAtencion = atencion && !atencion.fechaFin;

  const cargarTurnoActivo = useCallback(async () => {
    if (!user) return;
    setCargando(true);
    setErrorCarga(null);
    try {
      const data: any[] = await turnosService.listar();
      const activo = (data ?? []).find(
        (t) => t.asesorId === user.usuarioId && ESTADOS_ACTIVOS.includes(t.estado)
      );
      if (activo) {
        setTurno(activo);
        try {
          const att = await atencionesService.obtenerPorTurno(activo.id);
          setAtencion(att);
        } catch {
          setAtencion(null); // aún no hay atención registrada para este turno
        }
      } else {
        setTurno(null);
        setAtencion(null);
      }
    } catch (e: any) {
      setErrorCarga(e.message || 'No se pudo cargar el turno');
    } finally {
      setCargando(false);
    }
  }, [user]);

  useEffect(() => {
    if (sesionLoading) return;
    cargarTurnoActivo();
  }, [sesionLoading, cargarTurnoActivo]);

  const llamarSiguiente = useCallback(async () => {
    if (accionEnCurso || !user) return;
    setAccionEnCurso('llamar');
    try {
      const data = await turnosService.llamarSiguiente(Number(user.usuarioId));
      setTurno(data);
      setAtencion(null);
      setResultado('');
      setObservaciones('');
      toast.success(`Turno ${data.numeroTurno} llamado`);
    } catch (e: any) {
      toast.error(e.message || 'No se pudo llamar el turno');
    } finally {
      setAccionEnCurso(null);
    }
  }, [accionEnCurso, user]);

  const iniciarAtencion = useCallback(async () => {
    if (accionEnCurso || !user || !turno) return;
    setAccionEnCurso('iniciar');
    try {
      const data = await atencionesService.iniciar({ turnoId: turno.id, asesorId: Number(user.usuarioId) });
      setAtencion(data);
      setTurno((t: any) => (t ? { ...t, estado: 'EN_ATENCION' } : t));
      toast.success('Atención iniciada');
    } catch (e: any) {
      toast.error(e.message || 'No se pudo iniciar la atención');
    } finally {
      setAccionEnCurso(null);
    }
  }, [accionEnCurso, user, turno]);

  const pausar = useCallback(async () => {
    if (accionEnCurso || !user || !turno) return;
    setAccionEnCurso('pausar');
    try {
      const data = await atencionesService.pausar(turno.id, Number(user.usuarioId));
      setAtencion(data);
      setTurno((t: any) => (t ? { ...t, estado: 'EN_PAUSA' } : t));
      toast.success('Atención en pausa');
    } catch (e: any) {
      toast.error(e.message || 'No se pudo pausar');
    } finally {
      setAccionEnCurso(null);
    }
  }, [accionEnCurso, user, turno]);

  const reanudar = useCallback(async () => {
    if (accionEnCurso || !user || !turno) return;
    setAccionEnCurso('reanudar');
    try {
      const data = await atencionesService.reanudar(turno.id, Number(user.usuarioId));
      setAtencion(data);
      setTurno((t: any) => (t ? { ...t, estado: 'EN_ATENCION' } : t));
      toast.success('Atención reanudada');
    } catch (e: any) {
      toast.error(e.message || 'No se pudo reanudar');
    } finally {
      setAccionEnCurso(null);
    }
  }, [accionEnCurso, user, turno]);

  const finalizar = useCallback(async () => {
    if (accionEnCurso || !user || !turno) return;
    if (!resultado.trim()) {
      toast.error('El resultado es obligatorio (RN5)');
      return;
    }
    setAccionEnCurso('finalizar');
    try {
      const data = await atencionesService.finalizar({
        turnoId: turno.id,
        asesorId: Number(user.usuarioId),
        resultado,
        observaciones,
      });
      setAtencion(data);
      setTurno((t: any) => (t ? { ...t, estado: 'FINALIZADO' } : t));
      setResultado('');
      setObservaciones('');
      toast.success('Atención finalizada');
    } catch (e: any) {
      toast.error(e.message || 'No se pudo finalizar la atención');
    } finally {
      setAccionEnCurso(null);
    }
  }, [accionEnCurso, user, turno, resultado, observaciones]);

  // Atajos de teclado para acciones frecuentes (cuando el foco no está en un campo de texto)
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      const target = e.target as HTMLElement;
      const escribiendo = ['INPUT', 'TEXTAREA', 'SELECT'].includes(target.tagName) || target.isContentEditable;
      if (escribiendo) return;
      switch (e.key.toLowerCase()) {
        case 'l':
          e.preventDefault();
          llamarSiguiente();
          break;
        case 'i':
          e.preventDefault();
          iniciarAtencion();
          break;
        case 'p':
          e.preventDefault();
          pausar();
          break;
        case 'r':
          e.preventDefault();
          reanudar();
          break;
        case 'f':
          e.preventDefault();
          document.getElementById('resultado')?.focus();
          break;
      }
    };
    document.addEventListener('keydown', onKey);
    return () => document.removeEventListener('keydown', onKey);
  }, [llamarSiguiente, iniciarAtencion, pausar, reanudar]);

  if (!esPermitido) return null;

  return (
    <div className="page-gutter mx-auto max-w-7xl pt-6">
      <div className="mb-6 flex flex-wrap items-end justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Panel de asesor</h1>
          <p className="text-sm text-slate-500">Llama el siguiente turno y atiéndelo de principio a fin.</p>
        </div>
        <p className="hidden text-xs text-slate-400 sm:block" aria-hidden="true">
          Atajos: <kbd className="rounded border border-slate-200 bg-slate-50 px-1.5 py-0.5 font-mono">L</kbd> llamar ·{' '}
          <kbd className="rounded border border-slate-200 bg-slate-50 px-1.5 py-0.5 font-mono">I</kbd> iniciar ·{' '}
          <kbd className="rounded border border-slate-200 bg-slate-50 px-1.5 py-0.5 font-mono">P</kbd> pausar ·{' '}
          <kbd className="rounded border border-slate-200 bg-slate-50 px-1.5 py-0.5 font-mono">R</kbd> reanudar ·{' '}
          <kbd className="rounded border border-slate-200 bg-slate-50 px-1.5 py-0.5 font-mono">F</kbd> resultado
        </p>
      </div>

      {errorCarga && (
        <div className="mb-4">
          <Alert tipo="error" mensaje={errorCarga} onCerrar={() => setErrorCarga(null)} />
          <Button variant="outline" size="sm" className="mt-2" onClick={cargarTurnoActivo}>
            Reintentar
          </Button>
        </div>
      )}

      {cargando ? (
        <Card className="p-6">
          <Skeleton className="h-8 w-40" />
          <Skeleton className="mt-4 h-16 w-64" />
          <Skeleton className="mt-4 h-24 w-full" />
        </Card>
      ) : !turno ? (
        <Card className="flex flex-col items-center gap-4 p-10 text-center">
          <p className="text-sm text-slate-500">No hay un turno asignado en este momento.</p>
          <Button size="lg" onClick={llamarSiguiente} loading={accionEnCurso === 'llamar'}>
            Llamar siguiente turno
          </Button>
        </Card>
      ) : (
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
          {/* Turno actual */}
          <Card className="p-6 lg:col-span-2">
            <CardHeader
              titulo="Turno actual"
              accion={
                <Button variant="secondary" size="sm" onClick={llamarSiguiente} loading={accionEnCurso === 'llamar'}>
                  Siguiente
                </Button>
              }
            />
            <div className="flex flex-wrap items-end gap-6">
              <p className="font-mono text-4xl font-bold tracking-tight text-brand-800" aria-label={`Número de turno ${turno.numeroTurno}`}>
                {turno.numeroTurno}
              </p>
              <div aria-live="polite" role="status" className="flex items-center gap-2" key={estado}>
                <BadgeEstado estado={turno.estado} />
                <BadgePrioridad prioritario={turno.prioridad === 'PRIORITARIO'} />
              </div>
            </div>
            <p className="mt-4 text-sm text-slate-600">
              Ciudadano: <span className="font-semibold text-slate-900">{turno.ciudadanoNombre ?? `ID ${turno.ciudadanoId}`}</span>
              {' · '}Trámite: <span className="font-semibold text-slate-900">{turno.tipoTramiteNombre ?? turno.tipoTramiteId}</span>
            </p>

            {estado === 'LLAMADO' && (
              <div className="mt-6 rounded-lg border border-brand-200 bg-brand-50 p-4">
                <p className="text-sm font-medium text-brand-800">El ciudadano está listo para ser atendido.</p>
                <Button className="mt-3" onClick={iniciarAtencion} loading={accionEnCurso === 'iniciar'}>
                  Iniciar atención
                </Button>
              </div>
            )}

            {enAtencion && (
              <div className="mt-6 space-y-4">
                <div>
                  <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Atención en curso</p>
                  <div className="mt-2 flex gap-2">
                    <Button variant="secondary" onClick={pausar} disabled={estado !== 'EN_ATENCION'} loading={accionEnCurso === 'pausar'}>
                      Pausar
                    </Button>
                    <Button variant="secondary" onClick={reanudar} disabled={estado !== 'EN_PAUSA'} loading={accionEnCurso === 'reanudar'}>
                      Reanudar
                    </Button>
                  </div>
                </div>

                <div className="rounded-lg border border-slate-200 bg-slate-50 p-4">
                  <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Finalizar atención (RN5, RN6)</p>
                  <div className="mt-3">
                    <Input
                      id="resultado"
                      label="Resultado *"
                      placeholder="Ej. Documento actualizado"
                      value={resultado}
                      onChange={(e) => setResultado(e.target.value)}
                      required
                    />
                    <Input
                      label="Observaciones"
                      placeholder="Notas opcionales"
                      value={observaciones}
                      onChange={(e) => setObservaciones(e.target.value)}
                    />
                    <Button className="mt-1" onClick={finalizar} loading={accionEnCurso === 'finalizar'}>
                      Finalizar atención
                    </Button>
                  </div>
                </div>
              </div>
            )}

            {estado === 'FINALIZADO' && (
              <div className="mt-6 rounded-lg border border-green-200 bg-green-50 p-4">
                <p className="text-sm font-medium text-green-800">Turno finalizado. Puedes llamar al siguiente.</p>
                <Button className="mt-3" onClick={llamarSiguiente} loading={accionEnCurso === 'llamar'}>
                  Atender siguiente turno
                </Button>
              </div>
            )}
          </Card>

          {/* Resumen de la atención */}
          <Card className="p-6">
            <CardHeader titulo="Atención actual" />
            {atencion ? (
              <dl className="divide-y divide-slate-100 text-sm">
                {[
                  ['Resultado', atencion.resultado],
                  ['Tiempo de espera', atencion.tiempoEsperaMinutos != null ? `${atencion.tiempoEsperaMinutos} min` : '—'],
                  ['Tiempo de atención', atencion.tiempoAtencionMinutos != null ? `${atencion.tiempoAtencionMinutos} min` : '—'],
                  ['Pausas', atencion.totalPausasMinutos != null ? `${atencion.totalPausasMinutos} min` : '—'],
                ].map(([k, v]) => (
                  <div key={k as string} className="flex items-baseline justify-between gap-4 py-3">
                    <dt className="text-slate-500">{k}</dt>
                    <dd className="text-right font-semibold text-slate-900">{v}</dd>
                  </div>
                ))}
              </dl>
            ) : (
              <p className="text-sm text-slate-500">Aún no hay atención iniciada para este turno.</p>
            )}
          </Card>
        </div>
      )}
    </div>
  );
}
