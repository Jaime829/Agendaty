import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { usuariosService } from '@/api';
import { Button } from '@/components/Button';
import { Input } from '@/components/Input';
import { Select } from '@/components/Select';
import { Alert } from '@/components/Alert';
import { useAuthContext } from '@/context/AuthContext';
import { getRouteByRol } from '@/utils/roleRoutes';

export default function AdministradorPage() {
  const { user, tieneRol } = useAuthContext();
  const navigate = useNavigate();

  const [usuarios, setUsuarios] = useState<any[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({ nombre: '', correo: '', password: '', documento: '', telefono: '', rol: 'ROLE_ASESOR' });

  const esAdmin = !!user && tieneRol('ROLE_ADMINISTRADOR');

  const cerrarError = () => {
    setError(null);
    setFieldErrors({});
  };

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    if (!tieneRol('ROLE_ADMINISTRADOR')) {
      navigate(getRouteByRol(user.rol));
      return;
    }
    cargarUsuarios();
  }, [tieneRol, navigate, user]);

  if (!esAdmin) {
    return null;
  }

  const cargarUsuarios = async () => {
    setLoading(true);
    setFieldErrors({});
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
      setForm({ nombre: '', correo: '', password: '', documento: '', telefono: '', rol: 'ROLE_ASESOR' });
      cargarUsuarios();
    } catch (e: any) {
      setError(e.message || 'Error creando usuario');
      setFieldErrors(e.fieldErrors || {});
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold text-slate-900 mb-6">Administrador</h1>
      {error ? (
        <Alert tipo="error" mensaje={error} fieldErrors={fieldErrors} onCerrar={cerrarError} />
      ) : null}

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
              label="Correo"
              type="email"
              placeholder="usuario@email.com"
              value={form.correo}
              onChange={(e) => setForm({ ...form, correo: e.target.value })}
              required
            />
            <Input
              label="Documento"
              placeholder="12345678"
              pattern="\d{5,10}"
              title="El documento debe tener entre 5 y 10 dígitos"
              value={form.documento}
              onChange={(e) => setForm({ ...form, documento: e.target.value })}
              required
            />
            <Input
              label="Teléfono"
              placeholder="3001234567"
              pattern="\d{7,10}"
              title="El teléfono debe tener entre 7 y 10 dígitos"
              value={form.telefono}
              onChange={(e) => setForm({ ...form, telefono: e.target.value })}
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
                  <th className="pb-2 text-slate-500 font-semibold">Correo</th>
                  <th className="pb-2 text-slate-500 font-semibold">Rol</th>
                  <th className="pb-2 text-slate-500 font-semibold">Nombre</th>
                </tr>
              </thead>
              <tbody>
                {usuarios.map((u) => (
                  <tr key={u.id} className="border-b border-slate-100">
                    <td className="py-2">{u.correo}</td>
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
