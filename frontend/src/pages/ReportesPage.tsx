import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { reportesService } from '@/api';
import { Button } from '@/components/Button';
import { Input } from '@/components/Input';
import { Alert } from '@/components/Alert';
import { useAuth } from '@/hooks/useAuth';
import { getRouteByRol } from '@/utils/roleRoutes';

export default function ReportesPage() {
  const { tieneRol } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!tieneRol('COORDINADOR') && !tieneRol('ADMINISTRADOR')) {
      navigate(getRouteByRol(user?.rol));
    }
  }, [tieneRol, navigate, user?.rol]);

  if (!tieneRol('COORDINADOR') && !tieneRol('ADMINISTRADOR')) {
    return null;
  }

  const [fechaDesde, setFechaDesde] = useState('');
  const [fechaHasta, setFechaHasta] = useState('');
  const [reporte, setReporte] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const consultar = async () => {
    setError(null);
    setLoading(true);
    try {
      const r = await reportesService.tiempoEsperaPromedio({
        fechaDesde,
        fechaHasta,
      });
      setReporte(r);
    } catch (e: any) {
      setError(e.message || 'Error cargando reporte');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold text-slate-900 mb-6">Reportes</h1>
      {error && <Alert tipo="error" mensaje={error} onCerrar={() => setError(null)} />}

      <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
        <h2 className="text-lg font-bold mb-4">Tiempo promedio de espera</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
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
          />
          <Button onClick={consultar} loading={loading}>Consultar</Button>
        </div>

        {reporte && (
          <div className="bg-slate-50 p-4 rounded-lg border border-slate-200">
            <p className="text-sm text-slate-500">Tiempo promedio de espera</p>
            <p className="text-3xl font-bold text-blue-600">
              {reporte.valor} min
            </p>
            <p className="text-sm text-slate-500">
              Atenciones: {reporte.cantidad_atenciones}
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
