import { Button } from '@/components/Button';

interface ErrorStateProps {
  mensaje: string;
  onReintentar?: () => void;
}

export function ErrorState({ mensaje, onReintentar }: ErrorStateProps) {
  return (
    <div role="alert" className="rounded-lg border border-red-200 bg-red-50 px-5 py-6 text-center">
      <p className="text-sm font-semibold text-red-800">No se pudo completar la acción</p>
      <p className="mt-1 text-sm text-red-700">{mensaje}</p>
      {onReintentar && (
        <Button variant="outline" size="sm" className="mt-4" onClick={onReintentar}>
          Reintentar
        </Button>
      )}
    </div>
  );
}
