import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { turnosService, atencionesService } from '@/api';
import { BadgeEstado } from '@/components/BadgeEstado';
import { Button } from '@/components/Button';
import { Input } from '@/components/Input';
import { Alert } from '@/components/Alert';
import { Loading } from '@/components/Loading';
import { useAuth } from '@/hooks/useAuth';

export default function AsesorPage() {
  const { user, tieneRol } = useAuth();
  const navigate = useNavigate();
  const [turno, setTurno] = useState<any>(null);
  const [atencion, setAtencion] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [resultado, setResultado] = useState('');
  const [observaciones, setObservaciones] = useState('');

  useEffect(() => {
    if (!tieneRol('ASESOR')) {
      navigate('/');
      return;
    }
    cargarTurno();
  }, [tieneRol, navigate]);

  const cargarTurno = async () => {
    setLoading(true);
    try {
      const data = await turnosService.obtener(Number(user?.usuarioId ?? 1));
      setTurno(data);
      if (data.id) {
        try {
          const att = await atencionesService.obtenerPorTurno(data.id);
          setAtencion(att);
        } catch {
          // no hay atención aún
        }
      }
    } catch (e: any) {
      setError(e.message || 'Error cargando turno');
    } finally {
      setLoading(false);
    }
  };

  const llamarSiguiente = async () => {
    setError(null);
    setLoading(true);
    try {
      const data = await turnosService.llamarSiguiente(user?.usuarioId ?? 0);
      setTurno(data);
      setAtencion(null);
    } catch (e: any) {
      setError(e.message || 'Error llamando turno');
    } finally {
      setLoading(false);
    }
  };

  const iniciarAtencion = async () => {
    setError(null);
    setLoading(true);
    try {
      const data = await atencionesService.iniciar({
        turnoId: turno.id,
        asesorId: user?.usuarioId ?? 0,
      });
      setAtencion(data);
      setTurno((t) => (t ? { ...t, estado: 'EN_ATENCION' } : t));
    } catch (e: any) {
      setError(e.message || 'Error iniciando atención');
    } finally {
      setLoading(false);
    }
  };

  const pausar = async () => {
    try {
      const data = await atencionesService.pausar(turno.id, user?.usuarioId ?? 0);
      setAtencion(data);
      setTurno((t) => (t ? { ...t, estado: 'EN_PAUSA' } : t));
    } catch (e: any) {
      setError(e.message || 'Error pausando');
    }
  };

  const reanudar = async () => {
    try {
      const data = await atencionesService.reanudar(turno.id, user?.usuarioId ?? 0);
      setAtencion(data);
      setTurno((t) => (t ? { ...t, estado: 'EN_ATENCION' } : t));
    } catch (e: any) {
      setError(e.message || 'Error reanudando');
    }
  };

  const finalizar = async () => {
    if (!resultado.trim()) {
      setError('El resultado es obligatorio (RN5)');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      const data = await atencionesService.finalizar({
        turnoId: turno.id,
        asesorId: user?.usuarioId ?? 0,
        resultado,
        observaciones,
      });
      setAtencion(data);
      setTurno((t) => (t ? { ...t, estado: 'FINALIZADO' } : t));
      setResultado('');
      setObservaciones('');
    } catch (e: any) {
      setError(e.message || 'Error finalizando');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold text-slate-900 mb-6">Asesor</h1>
      {error && <Alert tipo="error" mensaje={error} onCerrar={() => setError(null)} />}

      {!turno ? (
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
          <Button onClick={llamarSiguiente} loading={loading}>
            Llamar siguiente turno (CA5)
          </Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
            <h2 className="text-lg font-bold mb-4">Turno actual: {turno.numeroTurno}</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
              <div>
                <p className="text-xs text-slate-500">Estado</p>
                <BadgeEstado estado={turno.estado} />
              </div>
              <div>
                <p className="text-xs text-slate-500">Prioridad</p>
                <span className={turno.prioridad === 'PRIORITARIO' ? 'text-yellow-600 font-bold' : 'text-slate-600'}>
                  {turno.prioridad}
                </span>
              </div>
            </div>
            <p className="text-slate-600 mb-4">Ciudadano: {turno.ciudadanoNombre}</p>

            {turno.estado === 'LLAMADO' && (
              <div className="mb-6 p-4 bg-blue-50 rounded-lg border border-blue-100">
                <p className="text-sm text-blue-800">El turno está listo para ser atendido.</p>
                <Button onClick={iniciarAtencion} loading={loading}>
                  Iniciar atención (RN1, RN4)
                </Button>
              </div>
            )}

            {atencion && (
              <div className="space-y-4">
                <div>
                  <p className="text-xs text-slate-500 mb-1">Acciones de atención</p>
                  <div className="flex gap-2">
                    {atencion && atencion.fechaFin ? null : (
                      <>
                        <Button variant="secondary" onClick={pausar} disabled={turno.estado !== 'EN_ATENCION'}>
                          Pausar
                        </Button>
                        <Button variant="secondary" onClick={reanudar} disabled={turno.estado !== 'EN_PAUSA'}>
                          Reanudar
                        </Button>
                      </>
                    )}
                  </div>
                </div>
                <div className="p-4 bg-slate-50 rounded-lg border border-slate-200">
                  <p className="text-xs text-slate-500 mb-2">Finalizar atención (RN5, RN6)</p>
                  <Input
                    label="Resultado *"
                    value={resultado}
                    onChange={(e) => setResultado(e.target.value)}
                    required
                  />
                  <Input
                    label="Observaciones"
                    value={observaciones}
                    onChange={(e) => setObservaciones(e.target.value)}
                  />
                  <Button onClick={finalizar} loading={loading} className="mt-2">
                    Finalizar atención
                  </Button>
                </div>
              </div>
            )}
          </div>

          <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
            <h2 className="text-lg font-bold mb-4">Atención actual</h2>
            {atencion ? (
              <div className="space-y-2 text-sm">
                <p><span className="font-semibold">Resultado:</span> {atencion.resultado}</p>
                <p><span className="font-semibold">Tiempo espera:</span> {atencion.tiempoEsperaMinutos} min</p>
                <p><span className="font-semibold">Tiempo atención:</span> {atencion.tiempoAtencionMinutos} min</p>
                <p><span className="font-semibold">Pausas:</span> {atencion.totalPausasMinutos} min</p>
              </div>
            ) : (
              <p className="text-slate-500">Sin atención iniciada.</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
