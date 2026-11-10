import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { usuariosService, tiposTramiteService, asesoresService } from '@/api';
import { Button } from '@/components/Button';
import { Input } from '@/components/Input';
import { Select } from '@/components/Select';
import { Alert } from '@/components/Alert';
import { Loading } from '@/components/Loading';
import { useAuth } from '@/hooks/useAuth';

export default function AdministradorPage() {
  const { user, tieneRol } = useAuth();
  const navigate = useNavigate();
  if (!tieneRol('ADMINISTRADOR')) {
    navigate('/');
    return null;
  }

  const [usuarios, setUsuarios] = useState<any[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({ nombre: '', username: '', password: '', email: '', rol: 'ROLE_CIUDADANO' });

  useEffect(() => {
    cargarUsuarios();
  }, []);

  const cargarUsuarios = async () => {
    setLoading(true);
    try {
      const data = await usuariosService.listar();
      setUsuarios(data);
    } catch (e: any) {
      setError(e.message || 'Error cargando usuarios');
    } finally {
      setLoading(false);
    }
  };

  const crearUsuario = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await usuariosService.crear(form);
      setForm({ nombre: '', username: '', password: '', email: '', rol: 'ROLE_CIUDADANO' });
      cargarUsuarios();
    } catch (e: any) {
      setError(e.message || 'Error creando usuario');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold text-slate-900 mb-6">Administrador</h1>
      {error && <Alert tipo="error" mensaje={error} onCerrar={() => setError(null)} />}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
          <h2 className="text-lg font-bold mb-4">Gestión de usuarios</h2>
          <form onSubmit={crearUsuario} className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
            <Input
              label="Nombre"
              value={form.nombre}
              onChange={(e) => setForm({ ...form, nombre: e.target.value })}
              required
            />
            <Input
              label="Usuario"
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              required
            />
            <Input
              label="Contraseña"
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
            />
            <Select
              label="Rol"
              options={[
                { value: 'ROLE_CIUDADANO', label: 'Ciudadano' },
                { value: 'ROLE_ASESOR', label: 'Asesor' },
                { value: 'ROLE_RECEPCIONISTA', label: 'Recepcionista' },
                { value: 'ROLE_COORDINADOR', label: 'Coordinador' },
                { value: 'ROLE_ADMINISTRADOR', label: 'Administrador' },
              ]}
              value={form.rol}
              onChange={(e) => setForm({ ...form, rol: e.target.value })}
            />
            <Button type="submit" loading={loading}>Crear usuario</Button>
          </form>

          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left">
              <thead>
                <tr className="border-b border-slate-200">
                  <th className="pb-2 text-slate-500 font-semibold">Usuario</th>
                  <th className="pb-2 text-slate-500 font-semibold">Rol</th>
                  <th className="pb-2 text-slate-500 font-semibold">Nombre</th>
                </tr>
              </thead>
              <tbody>
                {usuarios.map((u) => (
                  <tr key={u.id} className="border-b border-slate-100">
                    <td className="py-2">{u.username}</td>
                    <td className="py-2">{u.rol}</td>
                    <td className="py-2">{u.nombre}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
          <h2 className="text-lg font-bold mb-4">Módulos</h2>
          <div className="space-y-3">
            <Button variant="secondary" fullWidth onClick={() => navigate('/recepcionista')}>
              Recepcionista
            </Button>
            <Button variant="secondary" fullWidth onClick={() => navigate('/asesor')}>
              Asesor
            </Button>
            <Button variant="secondary" fullWidth onClick={() => navigate('/coordinador')}>
              Coordinador
            </Button>
            <Button variant="secondary" fullWidth onClick={() => navigate('/ciudadano')}>
              Ciudadano
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
