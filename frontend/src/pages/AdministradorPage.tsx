import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { usuariosService } from '@/api';
import {
  Button,
  Input,
  Select,
  Alert,
  Card,
  CardHeader,
  Skeleton,
  EmptyState,
} from '@/components';
import { useRolGuard } from '@/hooks/useRolGuard';

const ROL_LABEL: Record<string, string> = {
  ROLE_CIUDADANO: 'Ciudadano',
  ROLE_ASESOR: 'Asesor',
  ROLE_RECEPCIONISTA: 'Recepcionista',
  ROLE_COORDINADOR: 'Coordinador',
  ROLE_ADMINISTRADOR: 'Administrador',
};

export default function AdministradorPage() {
  const { esPermitido } = useRolGuard(['ROLE_ADMINISTRADOR']);
  const navigate = useNavigate();

  const [usuarios, setUsuarios] = useState<any[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [cargandoLista, setCargandoLista] = useState(true);
  const [creando, setCreando] = useState(false);
  const [form, setForm] = useState({ nombre: '', correo: '', password: '', documento: '', telefono: '', rol: 'ROLE_ASESOR' });

  // Nota: no pone cargandoLista=true aquí; el estado inicial ya es "cargando"
  // y react-hooks/set-state-in-effect prohíbe setState síncrono dentro de efectos.
  const cargarUsuarios = useCallback(async () => {
    try {
      const data = await usuariosService.listar();
      setUsuarios(data ?? []);
    } catch (e: any) {
      setError(e.message || 'No se pudo cargar la lista de usuarios');
    } finally {
      setCargandoLista(false);
    }
  }, []);

  useEffect(() => {
    if (!esPermitido) return;
    // Inline (no llamar a cargarUsuarios): la regla react-hooks/set-state-in-effect
    // no admite invocar desde el efecto funciones que llaman setState.
    usuariosService
      .listar()
      .then((data) => setUsuarios(data ?? []))
      .catch((e: any) => setError(e.message || 'No se pudo cargar la lista de usuarios'))
      .finally(() => setCargandoLista(false));
  }, [esPermitido]);

  const crearUsuario = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setFieldErrors({});
    setCreando(true);
    try {
      const creado = await usuariosService.crear(form);
      toast.success(`Usuario ${creado.correo ?? form.correo} creado`);
      setForm({ nombre: '', correo: '', password: '', documento: '', telefono: '', rol: 'ROLE_ASESOR' });
      cargarUsuarios();
    } catch (err: any) {
      setError(err.message || 'No se pudo crear el usuario');
      setFieldErrors(err.fieldErrors || {});
    } finally {
      setCreando(false);
    }
  };

  if (!esPermitido) return null;

  const modulos = [
    { ruta: '/recepcionista', etiqueta: 'Recepción' },
    { ruta: '/asesor', etiqueta: 'Asesor' },
    { ruta: '/coordinador', etiqueta: 'Coordinador' },
    { ruta: '/ciudadano', etiqueta: 'Ciudadano' },
  ];

  return (
    <div className="page-gutter mx-auto max-w-7xl pt-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Administración</h1>
        <p className="text-sm text-slate-500">Gestiona las cuentas internas y de ciudadanos del sistema.</p>
      </div>

      {error && (
        <div className="mb-4">
          <Alert tipo="error" mensaje={error} fieldErrors={fieldErrors} onCerrar={() => { setError(null); setFieldErrors({}); }} />
        </div>
      )}

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <Card className="p-6 lg:col-span-2">
          <CardHeader
            titulo="Usuarios del sistema"
            descripcion="Los ciudadanos también se crean por registro público; aquí puedes crearlos manualmente."
          />
          <form onSubmit={crearUsuario} className="grid grid-cols-1 gap-4 border-b border-slate-100 pb-6 md:grid-cols-2 xl:grid-cols-3">
            <Input
              label="Nombre completo *"
              autoComplete="name"
              value={form.nombre}
              onChange={(e) => setForm({ ...form, nombre: e.target.value })}
              required
            />
            <Input
              label="Correo *"
              type="email"
              autoComplete="email"
              placeholder="nombre@dominio.com"
              value={form.correo}
              onChange={(e) => setForm({ ...form, correo: e.target.value })}
              required
            />
            <Input
              label="Documento *"
              inputMode="numeric"
              placeholder="12345678"
              pattern="\d{5,10}"
              title="El documento debe tener entre 5 y 10 dígitos"
              value={form.documento}
              onChange={(e) => setForm({ ...form, documento: e.target.value })}
              required
            />
            <Input
              label="Teléfono *"
              inputMode="numeric"
              placeholder="3001234567"
              pattern="\d{7,10}"
              title="El teléfono debe tener entre 7 y 10 dígitos"
              value={form.telefono}
              onChange={(e) => setForm({ ...form, telefono: e.target.value })}
              required
            />
            <Input
              label="Contraseña *"
              type="password"
              autoComplete="new-password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
            />
            <Select
              label="Rol *"
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
            <Button type="submit" size="lg" className="md:col-span-2 xl:col-span-3" loading={creando}>
              Crear usuario
            </Button>
          </form>

          {cargandoLista ? (
            <div className="mt-6 space-y-3">
              {[0, 1, 2, 3].map((i) => (
                <Skeleton key={i} className="h-11 w-full" />
              ))}
            </div>
          ) : usuarios.length === 0 ? (
            <div className="mt-6">
              <EmptyState titulo="Aún no hay usuarios" descripcion="Crea el primer usuario interno con el formulario de arriba." />
            </div>
          ) : (
            <div className="-mx-2 mt-6 overflow-x-auto px-2">
              <table className="table-data">
                <thead>
                  <tr>
                    <th scope="col">Correo</th>
                    <th scope="col">Nombre</th>
                    <th scope="col">Rol</th>
                  </tr>
                </thead>
                <tbody>
                  {usuarios.map((u) => (
                    <tr key={u.id}>
                      <td className="text-slate-600">{u.correo}</td>
                      <td className="font-medium text-slate-900">{u.nombre}</td>
                      <td>
                        <span className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-1 text-xs font-semibold text-slate-700">
                          {ROL_LABEL[u.rol] ?? u.rol}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>

        <div className="space-y-6">
          <Card className="p-6">
            <CardHeader titulo="Módulos" descripcion="Abre cualquier vista de la plataforma." />
            <div className="space-y-2">
              {modulos.map(({ ruta, etiqueta }) => (
                <Button key={ruta} variant="secondary" fullWidth onClick={() => navigate(ruta)}>
                  {etiqueta}
                </Button>
              ))}
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
}
