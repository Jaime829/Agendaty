import { useState } from 'react';
import { turnosService } from '@/api';
import { BadgeEstado } from '@/components/BadgeEstado';
import { Button } from '@/components/Button';
import { Input } from '@/components/Input';
import { Alert } from '@/components/Alert';
import { EmptyState } from '@/components';

export default function CiudadanoPage() {
  const [numero, setNumero] = useState('');
  const [turno, setTurno] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const consultar = async () => {
    setError(null);
    setLoading(true);
    try {
      // Endpoint público simplificado: listar y filtrar por número (en producción habría un endpoint dedicado)
      const turnos = await turnosService.listar();
      const t = turnos.find((x: any) => x.numeroTurno === numero.trim().toUpperCase());
      if (t) setTurno(t);
      else setError('No se encontró un turno con ese número');
    } catch (e: any) {
      setError(e.message || 'Error consultando turno');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold text-slate-900 mb-6">Ciudadano — Consulta de turno</h1>

      <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm max-w-md">
        <p className="text-sm text-slate-600 mb-4">Ingrese el número de su turno para consultar su estado.</p>
        <div className="flex gap-2">
          <Input
            label="Número de turno"
            placeholder="CEDN-00001"
            value={numero}
            onChange={(e) => setNumero(e.target.value)}
          />
          <Button onClick={consultar} loading={loading}>Consultar</Button>
        </div>
        {error && <Alert tipo="error" mensaje={error} onCerrar={() => setError(null)} />}
      </div>

      {turno && (
        <div className="mt-6 bg-white p-6 rounded-xl border border-slate-200 shadow-sm max-w-md">
          <h2 className="text-lg font-bold mb-2">Turno {turno.numeroTurno}</h2>
          <div className="space-y-3 text-sm">
            <p><span className="font-semibold">Estado:</span> <BadgeEstado estado={turno.estado} /></p>
            <p><span className="font-semibold">Trámite:</span> {turno.tipoTramiteNombre}</p>
            <p><span className="font-semibold">Prioridad:</span> {turno.prioridad}</p>
            {turno.fechaLlamado && <p><span className="font-semibold">Llamado a las:</span> {turno.fechaLlamado}</p>}
          </div>
        </div>
      )}

      {!turno && numero && !error && (
        <EmptyState
          titulo="Turno no encontrado"
          descripcion="Verifique el número e intente nuevamente."
        />
      )}
    </div>
  );
}
