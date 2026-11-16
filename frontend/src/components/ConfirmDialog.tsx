import { Modal } from '@/components/Modal';
import { Button } from '@/components/Button';

interface ConfirmDialogProps {
  titulo: string;
  mensaje: string;
  confirmarTexto?: string;
  cancelarTexto?: string;
  onConfirmar: () => void;
  onCancelar: () => void;
  destructivo?: boolean;
  cargando?: boolean;
}

export function ConfirmDialog({
  titulo,
  mensaje,
  confirmarTexto = 'Confirmar',
  cancelarTexto = 'Cancelar',
  onConfirmar,
  onCancelar,
  destructivo = true,
  cargando = false,
}: ConfirmDialogProps) {
  return (
    <Modal
      titulo={titulo}
      onClose={onCancelar}
      pie={
        <>
          <Button variant="outline" onClick={onCancelar} disabled={cargando}>
            {cancelarTexto}
          </Button>
          <Button variant={destructivo ? 'destructive' : 'primary'} onClick={onConfirmar} loading={cargando}>
            {confirmarTexto}
          </Button>
        </>
      }
    >
      <p className="text-sm text-slate-600">{mensaje}</p>
    </Modal>
  );
}
