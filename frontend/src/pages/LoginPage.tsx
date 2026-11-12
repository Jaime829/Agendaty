import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '@/api/auth';
import { useAuthContext } from '@/context/AuthContext';
import { Button } from '@/components/Button';
import { Input } from '@/components/Input';
import { Alert } from '@/components/Alert';
import { Building2 } from 'lucide-react';
import type { LoginRequest } from '@/types';
import { getRouteByRol } from '@/utils/roleRoutes';

export default function LoginPage() {
  const navigate = useNavigate();
  const { login, loading } = useAuthContext();
  const [form, setForm] = useState<LoginRequest>({ correo: '', password: '' });
  const [error, setError] = useState<string | null>(null);
  const [submitLoading, setSubmitLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitLoading(true);
    try {
      const data = await authService.login(form);
      // En modo JWT-less, generamos un token simulado para el cliente
      const token = data.token || `demo-token-${data.id}-${Date.now()}`;
      authService.setToken(token);
      login(data.token, {
        usuarioId: data.id,
        username: data.correo,
        rol: data.rol,
      });
      // Navegar al dashboard correspondiente según el rol
      const rutaDestino = getRouteByRol(data.rol);
      navigate(rutaDestino);
      // Fallback: también actualizar window.location por si acaso
      window.location.href = rutaDestino;
    } catch (err: any) {
      setError(err.message || 'Credenciales inválidas');
    } finally {
      setSubmitLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-100">
      <div className="w-full max-w-md bg-white p-8 rounded-xl shadow-lg border border-slate-200">
        <div className="flex items-center gap-2 mb-6">
          <Building2 className="h-6 w-6 text-blue-600" />
          <h1 className="text-xl font-bold text-slate-900">Iniciar sesión</h1>
        </div>
        {error && <Alert tipo="error" mensaje={error} />}
        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="Correo electrónico"
            type="email"
            placeholder="juan@email.com"
            value={form.correo}
            onChange={(e) => setForm({ ...form, correo: e.target.value })}
            required
          />
          <Input
            label="Contraseña"
            type="password"
            placeholder="••••••••"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
          />
          <Button type="submit" className="w-full" loading={submitLoading}>
            Iniciar sesión
          </Button>
        </form>
        {loading && <p className="mt-4 text-sm text-slate-500">Cargando aplicación...</p>}
      </div>
    </div>
  );
}
