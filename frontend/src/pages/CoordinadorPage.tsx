import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { turnosService, reportesService, asesoresService } from '@/api';
import { BadgeEstado } from '@/components/BadgeEstado';
import { Alert } from '@/components/Alert';
import { Loading } from '@/components/Loading';
import { useAuth } from '@/hooks/useAuth';
import { getRouteByRol } from '@/utils/roleRoutes';

export default function CoordinadorPage() {
  const { user, tieneRol } = useAuth();
  const navigate = useNavigate();
  const [turnos, setTurnos] = useState<any[]>([]);
  const [reporte, setReporte] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!tieneRol('COORDINADOR') && !tieneRol('ADMINISTRADOR')) {
      navigate(getRouteByRol(user?.rol));
      return;
    }
    cargarTodo();
    const intervalo = setInterval(cargarTodo, 5000);
    return () => clearInterval(intervalo);
  }, [tieneRol, navigate]);

  const cargarTodo = async () => {
    try {
      const [t, r] = await Promise.all([
        turnosService.listar(),
        reportesService.tiempoEsperaPromedio(),
      ]);
      setTurnos(t);
      setReporte(r);
    } catch (e: any) {
      setError(e.message || 'Error cargando monitor');
    }
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold text-slate-900 mb-6">Coordinador de Atención</h1>
      {error && <Alert tipo="error" mensaje={error} onCerrar={() => setError(null)} />}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div className="bg-white p-4 rounded-xl border border-slate-200">
          <p className="text-xs text-slate-500">Turnos en espera</p>
          <p className="text-2xl font-bold">{turnos.filter((t) => t.estado === 'EN_ESPERA').length}</p>
        </div>
        <div className="bg-white p-4 rounded-xl border border-slate-200">
          <p className="text-xs text-slate-500">En atención</p>
          <p className="text-2xl font-bold">{turnos.filter((t) => t.estado === 'EN_ATENCION').length}</p>
        </div>
        <div className="bg-white p-4 rounded-xl border border-slate-200">
          <p className="text-xs text-slate-500">Tiempo espera promedio</p>
          <p className="text-2xl font-bold">
            {reporte?.valor != null ? reporte.valor : '—'} min
          </p>
        </div>
      </div>

      <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
        <h2 className="text-lg font-bold mb-4">Monitor de cola (tiempo casi real)</h2>
        {loading ? (
          <Loading />
        ) : turnos.length === 0 ? (
          <p className="text-slate-500">Sin turnos.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left">
              <thead>
                <tr className="border-b border-slate-200">
                  <th className="pb-2 text-slate-500 font-semibold">Número</th>
                  <th className="pb-2 text-slate-500 font-semibold">Estado</th>
                  <th className="pb-2 text-slate-500 font-semibold">Prioridad</th>
                  <th className="pb-2 text-slate-500 font-semibold">Asesor</th>
                </tr>
              </thead>
              <tbody>
                {turnos.map((t) => (
                  <tr key={t.id} className="border-b border-slate-100">
                    <td className="py-2 font-mono">{t.numeroTurno}</td>
                    <td className="py-2"><BadgeEstado estado={t.estado} /></td>
                    <td className="py-2">{t.prioridad === 'PRIORITARIO' ? '★' : ''}</td>
                    <td className="py-2">{t.asesorNombre || '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
