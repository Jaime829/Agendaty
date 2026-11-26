import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { Building2 } from 'lucide-react';
import { authService } from '@/api/auth';
import { useAuthContext } from '@/context/AuthContext';
import { Button } from '@/components/Button';
import { Input } from '@/components/Input';
import { Alert } from '@/components/Alert';
import { Card } from '@/components/Card';
import type { LoginRequest } from '@/types';
import { getHomeByRole } from '@/utils/roleRoutes';

export default function LoginPage() {
  const navigate = useNavigate();
  const { login, user, isAuthenticated } = useAuthContext();
  const [form, setForm] = useState<LoginRequest>({ correo: '', password: '' });
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
      const data = await authService.login(form);
      const token = data.token || `demo-token-${data.id}-${Date.now()}`;
      authService.setToken(token);

      login({
        token,
        tipo: 'Bearer',
        usuarioId: data.id,
        username: data.correo,
        rol: data.rol,
      });
      navigate(getHomeByRole(data.rol), { replace: true });
    } catch (err: any) {
      setError(err.message || 'Credenciales inválidas');
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
          <h1 className="text-xl font-bold text-slate-900">Iniciar sesión</h1>
        </div>
        <p className="mb-6 text-sm text-slate-500">Accede con el correo y la contraseña de tu cuenta Agendaty.</p>

        {error && <Alert tipo="error" mensaje={error} fieldErrors={fieldErrors} onCerrar={cerrarError} />}

        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="Correo electrónico"
            type="email"
            autoComplete="email"
            placeholder="nombre@dominio.com"
            value={form.correo}
            onChange={(e) => setForm({ ...form, correo: e.target.value })}
            required
          />
          <Input
            label="Contraseña"
            type="password"
            autoComplete="current-password"
            placeholder="••••••••"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
          />
          <Button type="submit" size="lg" fullWidth loading={enviando}>
            Iniciar sesión
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-600">
          ¿Aún no tienes cuenta?{' '}
          <Link to="/registro" className="font-medium text-brand-700 hover:text-brand-800 hover:underline">
            Regístrate como ciudadano
          </Link>
        </p>
      </Card>
    </div>
  );
}

