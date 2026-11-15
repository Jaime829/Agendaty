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
}

export function ConfirmDialog({
  titulo,
  mensaje,
  confirmarTexto = 'Confirmar',
  cancelarTexto = 'Cancelar',
  onConfirmar,
  onCancelar,
  destructivo = true,
}: ConfirmDialogProps) {
  return (
    <Modal
      titulo={titulo}
      onClose={onCancelar}
      pie={
        <>
          <Button variant="outline" onClick={onCancelar}>
            {cancelarTexto}
          </Button>
          <Button variant={destructivo ? 'destructive' : 'primary'} onClick={onConfirmar}>
            {confirmarTexto}
          </Button>
        </>
      }
    >
      <p className="text-sm text-slate-600">{mensaje}</p>
    </Modal>
  );
}
