import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { turnosService, tiposTramiteService } from '@/api';
import { BadgeEstado } from '@/components/BadgeEstado';
import { Button } from '@/components/Button';
import { Input } from '@/components/Input';
import { Select } from '@/components/Select';
import { Alert } from '@/components/Alert';
import { Loading, EmptyState } from '@/components';
import { useAuth } from '@/hooks/useAuth';
import type { TipoTramite } from '@/types';

export default function RecepcionistaPage() {
  const { user, tieneRol } = useAuth();
  const navigate = useNavigate();
  const [tipos, setTipos] = useState<TipoTramite[]>([]);
  const [turnos, setTurnos] = useState<any[]>([]);
  const [form, setForm] = useState({ ciudadanoId: '', tipoTramiteId: '', prioritario: false });
  const [prioritario, setPrioritario] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!tieneRol('RECEPCIONISTA') && !tieneRol('ADMINISTRADOR')) {
      navigate('/');
      return;
    }
    cargarTipos();
    cargarTurnos();
  }, [tieneRol, navigate]);

  const cargarTipos = async () => {
    try {
      const data = await tiposTramiteService.listar();
      setTipos(data);
    } catch (e: any) {
      setError(e.message || 'Error cargando trámites');
    }
  };

  const cargarTurnos = async () => {
    setLoading(true);
    try {
      const data = await turnosService.listar();
      setTurnos(data);
    } catch (e: any) {
      setError(e.message || 'Error cargando turnos');
    } finally {
      setLoading(false);
    }
  };

  const generarTurno = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setLoading(true);
    try {
      await turnosService.generar({
        usuarioId: user?.usuarioId ?? 0,
        turno: {
          ciudadanoId: Number(form.ciudadanoId),
          tipoTramiteId: Number(form.tipoTramiteId),
          prioritario,
        },
      });
      setSuccess('Turno generado correctamente');
      setForm({ ciudadanoId: '', tipoTramiteId: '', prioritario: false });
      setPrioritario(false);
      cargarTurnos();
    } catch (e: any) {
      setError(e.message || 'Error generando turno');
    } finally {
      setLoading(false);
    }
  };

  const cancelar = async (id: number) => {
    if (!window.confirm('¿Confirma cancelar este turno?')) return;
    try {
      await turnosService.cancelar(id, user?.usuarioId ?? 0);
      cargarTurnos();
    } catch (e: any) {
      setError(e.message || 'Error cancelando turno');
    }
  };

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Recepcionista</h1>
        <div className="flex items-center gap-3">
          {success && <Alert tipo="success" mensaje={success} onCerrar={() => setSuccess(null)} />}
          {error && <Alert tipo="error" mensaje={error} onCerrar={() => setError(null)} />}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Generar turno */}
        <div className="lg:col-span-2 bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
          <h2 className="text-lg font-bold mb-4">Generar turno (CA1-CA4)</h2>
          <form onSubmit={generarTurno} className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Input
              label="ID Ciudadano"
              placeholder="Ej. 1"
              value={form.ciudadanoId}
              onChange={(e) => setForm({ ...form, ciudadanoId: e.target.value })}
              required
            />
            <Select
              label="Tipo de trámite"
              options={tipos.map((t) => ({ value: String(t.id), label: `${t.codigo} - ${t.nombre}` }))}
              value={form.tipoTramiteId}
              onChange={(e) => setForm({ ...form, tipoTramiteId: e.target.value })}
              required
            />
            <div className="flex items-end">
              <label className="flex items-center gap-2 cursor-pointer">
                <input
                  type="checkbox"
                  checked={prioritario}
                  onChange={(e) => setPrioritario(e.target.checked)}
                  className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
                />
                Prioritario
              </label>
            </div>
            <Button type="submit" className="col-span-full md:col-span-1" loading={loading}>
              Generar turno
            </Button>
          </form>
          <p className="mt-3 text-xs text-slate-500">
            CA1: seleccione tipo de trámite · CA2: número único · CA3: estado En espera · CA4: prioritario/normal
          </p>
        </div>

        {/* Cola */}
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
          <h2 className="text-lg font-bold mb-4">Cola de turnos</h2>
          {loading ? (
            <Loading />
          ) : turnos.length === 0 ? (
            <EmptyState titulo="Sin turnos en la cola" descripcion="Genere un turno para comenzar." />
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm text-left">
                <thead>
                  <tr className="border-b border-slate-200">
                    <th className="pb-2 text-slate-500 font-semibold">Número</th>
                    <th className="pb-2 text-slate-500 font-semibold">Trámite</th>
                    <th className="pb-2 text-slate-500 font-semibold">Estado</th>
                    <th className="pb-2 text-slate-500 font-semibold">Prioridad</th>
                    <th className="pb-2 text-slate-500 font-semibold text-right">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {turnos.map((t) => (
                    <tr key={t.id} className="border-b border-slate-100">
                      <td className="py-2 font-mono">{t.numeroTurno}</td>
                      <td className="py-2">{t.tipoTramiteNombre}</td>
                      <td className="py-2"><BadgeEstado estado={t.estado} /></td>
                      <td className="py-2">{t.prioridad === 'PRIORITARIO' ? '★' : ''}</td>
                      <td className="py-2 text-right">
                        {t.estado === 'EN_ESPERA' && (
                          <Button
                            variant="destructive"
                            size="sm"
                            onClick={() => cancelar(t.id)}
                          >
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
        </div>
      </div>
    </div>
  );
}
