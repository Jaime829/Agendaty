import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '@/api/auth';
import { useAuthContext } from '@/context/AuthContext';
import { Button } from '@/components/Button';
import { Input } from '@/components/Input';
import { Alert } from '@/components/Alert';
import { Building2 } from 'lucide-react';
import type { RegisterRequest } from '@/types';

export default function RegistroPage() {
  const navigate = useNavigate();
  const { loading } = useAuthContext();
  const [form, setForm] = useState<RegisterRequest>({
    nombre: '',
    correo: '',
    password: '',
    documento: '',
    telefono: '',
  });
  const [error, setError] = useState<string | null>(null);
  const [submitLoading, setSubmitLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitLoading(true);
    try {
      await authService.registrar(form);
      setError(null);
      navigate('/login');
    } catch (err: any) {
      setError(err.message || 'Error al registrar usuario');
    } finally {
      setSubmitLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50">
      <nav className="bg-white border-b border-slate-200 sticky top-0 z-20">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <Link to="/" className="flex items-center gap-2">
            <Building2 className="h-7 w-7 text-blue-600" />
            <span className="text-xl font-bold text-slate-900 tracking-tight">Agendaty</span>
          </Link>
        </div>
      </nav>

      <div className="min-h-screen flex items-center justify-center bg-slate-100">
        <div className="w-full max-w-md bg-white p-8 rounded-xl shadow-lg border border-slate-200">
          <div className="flex items-center gap-2 mb-6">
            <Building2 className="h-6 w-6 text-blue-600" />
            <h1 className="text-xl font-bold text-slate-900">Crear cuenta</h1>
          </div>
          <p className="text-sm text-slate-500 mb-6">
            Regístrate como ciudadano. Tu rol será siempre CIUDADANO.
          </p>
          {error && <Alert tipo="error" mensaje={error} />}
          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="Nombre completo"
              placeholder="Juan Pérez"
              value={form.nombre}
              onChange={(e) => setForm({ ...form, nombre: e.target.value })}
              required
            />
            <Input
              label="Correo electrónico"
              type="email"
              placeholder="juan@email.com"
              value={form.correo}
              onChange={(e) => setForm({ ...form, correo: e.target.value })}
              required
            />
            <Input
              label="Documento de identidad"
              placeholder="12345678"
              value={form.documento}
              onChange={(e) => setForm({ ...form, documento: e.target.value })}
              required
            />
            <Input
              label="Teléfono"
              placeholder="3001234567"
              value={form.telefono}
              onChange={(e) => setForm({ ...form, telefono: e.target.value })}
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
              Crear cuenta
            </Button>
          </form>
          <p className="mt-6 text-center text-sm text-slate-600">
            ¿Ya tienes cuenta?{' '}
            <Link to="/login" className="text-blue-600 hover:text-blue-700 font-medium">
              Inicia sesión
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
