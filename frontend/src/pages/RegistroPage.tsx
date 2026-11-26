import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { Building2 } from 'lucide-react';
import { toast } from 'sonner';
import { authService } from '@/api/auth';
import { useAuthContext } from '@/context/AuthContext';
import { Button } from '@/components/Button';
import { Input } from '@/components/Input';
import { Alert } from '@/components/Alert';
import { Card } from '@/components/Card';
import type { RegisterRequest } from '@/types';
import { getHomeByRole } from '@/utils/roleRoutes';

export default function RegistroPage() {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuthContext();
  const [form, setForm] = useState<RegisterRequest>({
    nombre: '',
    correo: '',
    password: '',
    documento: '',
    telefono: '',
  });
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [enviando, setEnviando] = useState(false);

  if (isAuthenticated) {
    return <Navigate to={getHomeByRole(user?.rol)} replace />;
  }

  const cerrarError = () => {
    setError(null);
    setFieldErrors({});
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setFieldErrors({});
    setEnviando(true);
    try {
      await authService.registrar(form);
      toast.success('Cuenta creada. Ya puedes iniciar sesión.');
      navigate('/login', { replace: true });
    } catch (err: any) {
      setError(err.message || 'No se pudo crear la cuenta');
      setFieldErrors(err.fieldErrors || {});
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div className="page-gutter flex min-h-[calc(100vh-4rem)] items-center justify-center py-10">
      <Card className="w-full max-w-md p-8">
        <div className="mb-6 flex items-center gap-2">
          <Building2 className="h-6 w-6 text-brand-600" aria-hidden="true" />
          <h1 className="text-xl font-bold text-slate-900">Crear cuenta</h1>
        </div>
        <p className="mb-6 text-sm text-slate-500">
          Regístrate como ciudadano: tu rol será siempre CIUDADANO. Las cuentas de asesores y demás personal las crea
          un administrador.
        </p>

        {error && <Alert tipo="error" mensaje={error} fieldErrors={fieldErrors} onCerrar={cerrarError} />}

        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="Nombre completo"
            autoComplete="name"
            placeholder="Juan Pérez"
            value={form.nombre}
            onChange={(e) => setForm({ ...form, nombre: e.target.value })}
            required
          />
          <Input
            label="Correo electrónico"
            type="email"
            autoComplete="email"
            placeholder="juan@email.com"
            value={form.correo}
            onChange={(e) => setForm({ ...form, correo: e.target.value })}
            required
          />
          <Input
            label="Documento de identidad"
            inputMode="numeric"
            pattern="\d{5,10}"
            title="El documento debe tener entre 5 y 10 dígitos"
            placeholder="12345678"
            value={form.documento}
            onChange={(e) => setForm({ ...form, documento: e.target.value })}
            required
          />
          <Input
            label="Teléfono"
            inputMode="numeric"
            pattern="\d{7,10}"
            title="El teléfono debe tener entre 7 y 10 dígitos"
            placeholder="3001234567"
            value={form.telefono}
            onChange={(e) => setForm({ ...form, telefono: e.target.value })}
            required
          />
          <Input
            label="Contraseña"
            type="password"
            autoComplete="new-password"
            placeholder="Mínimo 6 caracteres"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
          />
          <Button type="submit" size="lg" fullWidth loading={enviando}>
            Crear cuenta
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-600">
          ¿Ya tienes cuenta?{' '}
          <Link to="/login" className="font-medium text-brand-700 hover:text-brand-800 hover:underline">
            Inicia sesión
          </Link>
        </p>
      </Card>
    </div>
  );
}
