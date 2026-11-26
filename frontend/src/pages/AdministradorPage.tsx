import { useState, useEffect, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { usuariosService, ventanillasService, tiposTramiteService } from '@/api';
import {
  PageHeader,
  KpiCard,
  DataTable,
  Button,
  Input,
  Select,
  Alert,
  Skeleton,
  EmptyState,
  Drawer,
  ConfirmDialog,
  RolBadge,
} from '@/components';
import { useRolGuard } from '@/hooks/useRolGuard';
import type { VentanillaDto } from '@/api';
import type { TipoTramite } from '@/types';
import type { DataTableColumn } from '@/components';

type Tab = 'usuarios' | 'ventanillas' | 'tramites';
type UsuarioDto = Awaited<ReturnType<typeof usuariosService.listar>>[number];

const TABS: { id: Tab; etiqueta: string }[] = [
  { id: 'usuarios', etiqueta: 'Usuarios' },
  { id: 'ventanillas', etiqueta: 'Ventanillas' },
  { id: 'tramites', etiqueta: 'Tipos de trámite' },
];

const ROL_OPTIONS = [
  { value: 'ROLE_CIUDADANO', label: 'Ciudadano' },
  { value: 'ROLE_ASESOR', label: 'Asesor' },
  { value: 'ROLE_RECEPCIONISTA', label: 'Recepcionista' },
  { value: 'ROLE_COORDINADOR', label: 'Coordinador' },
  { value: 'ROLE_ADMINISTRADOR', label: 'Administrador' },
];

const FORM_VACIO = { nombre: '', correo: '', password: '', documento: '', telefono: '', rol: 'ROLE_ASESOR' };

export default function AdministradorPage() {
  const { esPermitido } = useRolGuard(['ROLE_ADMINISTRADOR']);
  const navigate = useNavigate();

  const [tab, setTab] = useState<Tab>('usuarios');
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [usuarios, setUsuarios] = useState<UsuarioDto[]>([]);
  const [ventanillas, setVentanillas] = useState<VentanillaDto[]>([]);
  const [tramites, setTramites] = useState<TipoTramite[]>([]);

  // Drawer: nuevo usuario
  const [drawerUsuario, setDrawerUsuario] = useState(false);
  const [formUsuario, setFormUsuario] = useState(FORM_VACIO);
  const [creandoUsuario, setCreandoUsuario] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [errorForm, setErrorForm] = useState<string | null>(null);

  // Drawer: ventanilla (crear/editar)
  const [drawerVentanilla, setDrawerVentanilla] = useState<{ modo: 'crear' | 'editar'; id?: number } | null>(null);
  const [formVentanilla, setFormVentanilla] = useState({ numero: '', descripcion: '' });
  const [guardandoVentanilla, setGuardandoVentanilla] = useState(false);

  // Drawer: tipo de trámite (crear/editar)
  const [drawerTramite, setDrawerTramite] = useState<{ modo: 'crear' | 'editar'; id?: number } | null>(null);
  const [formTramite, setFormTramite] = useState({ nombre: '', codigo: '', esPrioritarioPorDefecto: false, activo: true });
  const [guardandoTramite, setGuardandoTramite] = useState(false);

  // Confirmación de borrado (ventanilla/trámite)
  const [confirmarEliminar, setConfirmarEliminar] = useState<{ tipo: 'ventanilla' | 'tramite'; id: number; etiqueta: string } | null>(null);
  const [eliminando, setEliminando] = useState(false);

  const fetchTodos = useCallback(
    () =>
      Promise.all([
        usuariosService.listar(),
        ventanillasService.listar(),
        tiposTramiteService.listar(),
      ]).then(([u, v, t]) => ({ u: u ?? [], v: v ?? [], t: t ?? [] })),
    []
  );

  // Nota: el efecto no puede llamar a cargarDatos() (react-hooks/set-state-in-effect);
  // ambas usan fetchTodos() y solo difieren en la gestión de estados.
  const cargarDatos = useCallback(async () => {
    try {
      const { u, v, t } = await fetchTodos();
      setUsuarios(u);
      setVentanillas(v);
      setTramites(t);
      setErrorCarga(null);
    } catch (e: any) {
      setErrorCarga(e.message || 'No se pudo cargar la información');
    } finally {
      setCargando(false);
    }
  }, [fetchTodos]);

  useEffect(() => {
    if (!esPermitido) return;
    fetchTodos()
      .then(({ u, v, t }) => {
        setUsuarios(u);
        setVentanillas(v);
        setTramites(t);
        setErrorCarga(null);
      })
      .catch((e: any) => setErrorCarga(e.message || 'No se pudo cargar la información'))
      .finally(() => setCargando(false));
  }, [esPermitido, fetchTodos]);

  const kpis = useMemo(() => {
    const total = usuarios.length;
    const ciudadanos = usuarios.filter((u) => u.rol === 'ROLE_CIUDADANO').length;
    const asesores = usuarios.filter((u) => u.rol === 'ROLE_ASESOR').length;
    const internos = total - ciudadanos;
    return { total, ciudadanos, asesores, internos };
  }, [usuarios]);

  const columnasUsuario: DataTableColumn<UsuarioDto>[] = [
    { key: 'correo', header: 'Correo', sortValue: (u) => u.correo.toLowerCase() },
    { key: 'nombre', header: 'Nombre', sortValue: (u) => u.nombre.toLowerCase() },
    { key: 'rol', header: 'Rol', render: (u) => <RolBadge rol={u.rol} />, sortValue: (u) => u.rol },
  ];

  const crearUsuario = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorForm(null);
    setFieldErrors({});
    setCreandoUsuario(true);
    try {
      const creado = await usuariosService.crear(formUsuario);
      toast.success(`Usuario ${creado.correo ?? formUsuario.correo} creado`);
      setFormUsuario(FORM_VACIO);
      setDrawerUsuario(false);
      cargarDatos();
    } catch (err: any) {
      setErrorForm(err.message || 'No se pudo crear el usuario');
      setFieldErrors(err.fieldErrors || {});
    } finally {
      setCreandoUsuario(false);
    }
  };

  const guardarVentanilla = async (e: React.FormEvent) => {
    e.preventDefault();
    setGuardandoVentanilla(true);
    try {
      const payload = { numero: Number(formVentanilla.numero), descripcion: formVentanilla.descripcion.trim() };
      if (drawerVentanilla?.modo === 'editar' && drawerVentanilla.id != null) {
        await ventanillasService.actualizar(drawerVentanilla.id, payload);
        toast.success('Ventanilla actualizada');
      } else {
        await ventanillasService.crear(payload);
        toast.success('Ventanilla creada');
      }
      setDrawerVentanilla(null);
      setFormVentanilla({ numero: '', descripcion: '' });
      cargarDatos();
    } catch (e: any) {
      toast.error(e.message || 'No se pudo guardar la ventanilla');
    } finally {
      setGuardandoVentanilla(false);
    }
  };

  const guardarTramite = async (e: React.FormEvent) => {
    e.preventDefault();
    setGuardandoTramite(true);
    try {
      const payload = {
        nombre: formTramite.nombre.trim(),
        codigo: formTramite.codigo.trim().toUpperCase(),
        esPrioritarioPorDefecto: formTramite.esPrioritarioPorDefecto,
        activo: formTramite.activo,
      };
      if (drawerTramite?.modo === 'editar' && drawerTramite.id != null) {
        await tiposTramiteService.actualizar(drawerTramite.id, payload);
        toast.success('Tipo de trámite actualizado');
      } else {
        await tiposTramiteService.crear(payload);
        toast.success('Tipo de trámite creado');
      }
      setDrawerTramite(null);
      setFormTramite({ nombre: '', codigo: '', esPrioritarioPorDefecto: false, activo: true });
      cargarDatos();
    } catch (e: any) {
      toast.error(e.message || 'No se pudo guardar el tipo de trámite');
    } finally {
      setGuardandoTramite(false);
    }
  };

  const eliminarSeleccionado = async () => {
    if (!confirmarEliminar) return;
    setEliminando(true);
    try {
      if (confirmarEliminar.tipo === 'ventanilla') {
        await ventanillasService.eliminar(confirmarEliminar.id);
        toast.success('Ventanilla eliminada');
      } else {
        await tiposTramiteService.eliminar(confirmarEliminar.id);
        toast.success('Tipo de trámite eliminado');
      }
      setConfirmarEliminar(null);
      cargarDatos();
    } catch (e: any) {
      toast.error(e.message || 'No se pudo eliminar');
    } finally {
      setEliminando(false);
    }
  };

  if (!esPermitido) return null;

  const columnasVentanilla: DataTableColumn<VentanillaDto>[] = [
    { key: 'numero', header: 'Número', sortValue: (v) => v.numero },
    { key: 'descripcion', header: 'Descripción', sortValue: (v) => (v.descripcion ?? '').toLowerCase() },
    {
      key: 'asesorId',
      header: 'Asesor',
      render: (v) => (v.asesorId != null ? `#${v.asesorId}` : <span className="text-slate-400">Sin asignar</span>),
    },
    {
      key: 'acciones',
      header: '',
      align: 'right',
      render: (v) => (
        <div className="flex justify-end gap-2">
          <Button variant="ghost" size="sm" onClick={() => { setFormVentanilla({ numero: String(v.numero), descripcion: v.descripcion ?? '' }); setDrawerVentanilla({ modo: 'editar', id: v.id }); }}>
            Editar
          </Button>
          <Button variant="destructive" size="sm" onClick={() => setConfirmarEliminar({ tipo: 'ventanilla', id: v.id, etiqueta: `Ventanilla ${v.numero}` })}>
            Eliminar
          </Button>
        </div>
      ),
    },
  ];

  const columnasTramite: DataTableColumn<TipoTramite>[] = [
    { key: 'codigo', header: 'Código', sortValue: (t) => t.codigo },
    { key: 'nombre', header: 'Nombre', sortValue: (t) => t.nombre.toLowerCase() },
    {
      key: 'esPrioritarioPorDefecto',
      header: 'Prioritario',
      render: (t) => (t.esPrioritarioPorDefecto ? <span className="text-amber-800">Sí</span> : <span className="text-slate-400">No</span>),
    },
    {
      key: 'activo',
      header: 'Estado',
      render: (t) => (t.activo ? <span className="font-semibold text-green-700">Activo</span> : <span className="text-slate-400">Inactivo</span>),
    },
    {
      key: 'acciones',
      header: '',
      align: 'right',
      render: (t) => (
        <div className="flex justify-end gap-2">
          <Button variant="ghost" size="sm" onClick={() => { setFormTramite({ nombre: t.nombre, codigo: t.codigo, esPrioritarioPorDefecto: t.esPrioritarioPorDefecto, activo: t.activo }); setDrawerTramite({ modo: 'editar', id: t.id }); }}>
            Editar
          </Button>
          <Button variant="destructive" size="sm" onClick={() => setConfirmarEliminar({ tipo: 'tramite', id: t.id, etiqueta: `${t.codigo} — ${t.nombre}` })}>
            Eliminar
          </Button>
        </div>
      ),
    },
  ];

  return (
    <div className="page-gutter mx-auto max-w-7xl pt-6">
      <PageHeader
        titulo="Administración"
        descripcion="Gestiona usuarios, ventanillas y tipos de trámite."
        accion={
          <div className="flex items-center gap-2">
            <Button variant="outline" size="sm" onClick={() => navigate('/coordinador')}>
              Ver cola en vivo →
            </Button>
            <Button onClick={() => setDrawerUsuario(true)}>Nuevo usuario</Button>
          </div>
        }
      />

      {errorCarga && (
        <div className="mb-4">
          <Alert tipo="error" mensaje={errorCarga} onCerrar={() => setErrorCarga(null)} />
          <Button variant="outline" size="sm" className="mt-2" onClick={cargarDatos}>Reintentar</Button>
        </div>
      )}

      {/* KPIs */}
      <div className="grid animate-rise-in grid-cols-2 gap-3 sm:grid-cols-4" aria-label="Resumen de usuarios">
        {cargando ? (
          <>
            {[0, 1, 2, 3].map((i) => <Skeleton key={i} className="h-24 w-full rounded-xl" />)}
          </>
        ) : (
          <>
            <KpiCard etiqueta="Usuarios totales" valor={kpis.total} destacado detalle={`${kpis.ciudadanos} ciudadanos · ${kpis.internos} internos`} />
            <KpiCard etiqueta="Ciudadanos" valor={kpis.ciudadanos} detalle="Auto-registrados" />
            <KpiCard etiqueta="Personal interno" valor={kpis.internos} detalle="Asesores, recepción, coord. y admin" />
            <KpiCard etiqueta="Asesores" valor={kpis.asesores} detalle="Capacidad operativa" />
          </>
        )}
      </div>

      {/* Tabs */}
      <div className="mt-6 border-b border-slate-200" role="tablist" aria-label="Secciones de administración">
        <div className="flex gap-1 overflow-x-auto">
          {TABS.map((t) => (
            <button
              key={t.id}
              role="tab"
              id={`tab-${t.id}`}
              aria-selected={tab === t.id}
              aria-controls={`panel-${t.id}`}
              onClick={() => setTab(t.id)}
              className={`whitespace-nowrap border-b-2 px-4 py-2.5 text-sm font-semibold transition-colors ${
                tab === t.id
                  ? 'border-brand-600 text-brand-800'
                  : 'border-transparent text-slate-500 hover:text-slate-800'
              }`}
            >
              {t.etiqueta}
            </button>
          ))}
        </div>
      </div>

      {/* Panel USUARIOS */}
      {tab === 'usuarios' && (
        <div id="panel-usuarios" role="tabpanel" aria-labelledby="tab-usuarios" className="pt-4">
          {cargando ? (
            <div className="space-y-3">{[0, 1, 2, 3].map((i) => <Skeleton key={i} className="h-11 w-full" />)}</div>
          ) : usuarios.length === 0 ? (
            <EmptyState
              titulo="Aún no hay usuarios"
              descripcion="Crea el primero con el botón «Nuevo usuario»."
              accion={<Button onClick={() => setDrawerUsuario(true)}>Nuevo usuario</Button>}
            />
          ) : (
            <DataTable
              columns={columnasUsuario}
              rows={usuarios}
              rowKey={(u) => u.id}
              searchText={(u) => `${u.correo} ${u.nombre}`}
              searchPlaceholder="Buscar por correo o nombre…"
              filterValue={(u) => u.rol}
              filterOptions={ROL_OPTIONS}
              filterLabel="Rol"
              pageSize={10}
              caption="Tabla de usuarios del sistema"
            />
          )}
        </div>
      )}

      {/* Panel VENTANILLAS */}
      {tab === 'ventanillas' && (
        <div id="panel-ventanillas" role="tabpanel" aria-labelledby="tab-ventanillas" className="pt-4">
          <div className="mb-3 flex items-center justify-between">
            <p className="text-sm text-slate-500">{ventanillas.length} ventanillas registradas</p>
            <Button variant="secondary" size="sm" onClick={() => { setFormVentanilla({ numero: '', descripcion: '' }); setDrawerVentanilla({ modo: 'crear' }); }}>
              Nueva ventanilla
            </Button>
          </div>
          {cargando ? (
            <div className="space-y-3">{[0, 1, 2].map((i) => <Skeleton key={i} className="h-11 w-full" />)}</div>
          ) : (
            <DataTable
              columns={columnasVentanilla}
              rows={ventanillas}
              rowKey={(v) => v.id}
              searchText={(v) => `${v.numero} ${v.descripcion ?? ''}`}
              searchPlaceholder="Buscar ventanilla…"
              pageSize={10}
              caption="Tabla de ventanillas"
            />
          )}
        </div>
      )}

      {/* Panel TRÁMITES */}
      {tab === 'tramites' && (
        <div id="panel-tramites" role="tabpanel" aria-labelledby="tab-tramites" className="pt-4">
          <div className="mb-3 flex items-center justify-between">
            <p className="text-sm text-slate-500">{tramites.filter((t) => t.activo).length} activos · {tramites.length} en total</p>
            <Button variant="secondary" size="sm" onClick={() => { setFormTramite({ nombre: '', codigo: '', esPrioritarioPorDefecto: false, activo: true }); setDrawerTramite({ modo: 'crear' }); }}>
              Nuevo trámite
            </Button>
          </div>
          {cargando ? (
            <div className="space-y-3">{[0, 1, 2].map((i) => <Skeleton key={i} className="h-11 w-full" />)}</div>
          ) : (
            <DataTable
              columns={columnasTramite}
              rows={tramites}
              rowKey={(t) => t.id}
              searchText={(t) => `${t.codigo} ${t.nombre}`}
              searchPlaceholder="Buscar trámite…"
              pageSize={10}
              caption="Tabla de tipos de trámite"
            />
          )}
        </div>
      )}

      {/* Drawer: Nuevo usuario */}
      {drawerUsuario && (
        <Drawer
          titulo="Nuevo usuario"
          descripcion="Crea cuentas internas o de ciudadanos. Los campos con * son obligatorios."
          onClose={() => { setDrawerUsuario(false); setErrorForm(null); setFieldErrors({}); }}
        >
          <form onSubmit={crearUsuario} className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            {errorForm && <Alert tipo="error" mensaje={errorForm} fieldErrors={fieldErrors} onCerrar={() => { setErrorForm(null); setFieldErrors({}); }} />}
            <div className="sm:col-span-2">
              <Input label="Nombre completo *" autoComplete="name" value={formUsuario.nombre} onChange={(e) => setFormUsuario({ ...formUsuario, nombre: e.target.value })} required />
            </div>
            <div className="sm:col-span-2">
              <Input label="Correo *" type="email" autoComplete="email" placeholder="nombre@dominio.com" value={formUsuario.correo} onChange={(e) => setFormUsuario({ ...formUsuario, correo: e.target.value })} required />
            </div>
            <Input
              label="Documento *"
              inputMode="numeric"
              placeholder="12345678"
              pattern="\d{5,10}"
              title="El documento debe tener entre 5 y 10 dígitos"
              value={formUsuario.documento}
              onChange={(e) => setFormUsuario({ ...formUsuario, documento: e.target.value })}
              required
            />
            <Input
              label="Teléfono *"
              inputMode="numeric"
              placeholder="3001234567"
              pattern="\d{7,10}"
              title="El teléfono debe tener entre 7 y 10 dígitos"
              value={formUsuario.telefono}
              onChange={(e) => setFormUsuario({ ...formUsuario, telefono: e.target.value })}
              required
            />
            <Input
              label="Contraseña *"
              type="password"
              autoComplete="new-password"
              hint="Mínimo 6 caracteres."
              value={formUsuario.password}
              onChange={(e) => setFormUsuario({ ...formUsuario, password: e.target.value })}
              required
            />
            <Select
              label="Rol *"
              options={ROL_OPTIONS}
              value={formUsuario.rol}
              onChange={(e) => setFormUsuario({ ...formUsuario, rol: e.target.value })}
            />
            <div className="sm:col-span-2 flex items-center justify-between gap-3 border-t border-slate-100 pt-4">
              <p className="text-xs text-slate-500">* Obligatorio. El menú rol interno lo crea un administrador.</p>
              <div className="flex gap-2">
                <Button type="button" variant="outline" onClick={() => { setDrawerUsuario(false); setErrorForm(null); setFieldErrors({}); }}>
                  Cancelar
                </Button>
                <Button type="submit" loading={creandoUsuario}>Crear usuario</Button>
              </div>
            </div>
          </form>
        </Drawer>
      )}

      {/* Drawer: Ventanilla */}
      {drawerVentanilla && (
        <Drawer
          titulo={drawerVentanilla.modo === 'crear' ? 'Nueva ventanilla' : 'Editar ventanilla'}
          descripcion="Número único y descripción visible para el ciudadano."
          onClose={() => setDrawerVentanilla(null)}
        >
          <form onSubmit={guardarVentanilla} className="space-y-4">
            <Input label="Número *" type="number" min={1} value={formVentanilla.numero} onChange={(e) => setFormVentanilla({ ...formVentanilla, numero: e.target.value })} required />
            <Input label="Descripción *" placeholder="Atención primera vez" value={formVentanilla.descripcion} onChange={(e) => setFormVentanilla({ ...formVentanilla, descripcion: e.target.value })} required />
            <div className="flex justify-end gap-2 border-t border-slate-100 pt-4">
              <Button type="button" variant="outline" onClick={() => setDrawerVentanilla(null)}>Cancelar</Button>
              <Button type="submit" loading={guardandoVentanilla}>{drawerVentanilla.modo === 'crear' ? 'Crear ventanilla' : 'Guardar cambios'}</Button>
            </div>
          </form>
        </Drawer>
      )}

      {/* Drawer: Tipo de trámite */}
      {drawerTramite && (
        <Drawer
          titulo={drawerTramite.modo === 'crear' ? 'Nuevo tipo de trámite' : 'Editar tipo de trámite'}
          descripcion="El código se usa para numerar turnos (ej. CED-00001)."
          onClose={() => setDrawerTramite(null)}
        >
          <form onSubmit={guardarTramite} className="space-y-4">
            <Input label="Nombre *" placeholder="Cédula de Identidad" value={formTramite.nombre} onChange={(e) => setFormTramite({ ...formTramite, nombre: e.target.value })} required />
            <Input label="Código *" placeholder="CED" maxLength={10} hint="Máximo 10 caracteres, se guardará en mayúsculas." value={formTramite.codigo} onChange={(e) => setFormTramite({ ...formTramite, codigo: e.target.value })} required />
            <fieldset className="space-y-2">
              <legend className="text-sm font-medium text-slate-700">Prioritario por defecto</legend>
              <div className="flex gap-2">
                {[{ v: false, t: 'Normal' }, { v: true, t: 'Prioritario' }].map(({ v, t }) => (
                  <label key={String(v)} className={`flex h-11 cursor-pointer items-center gap-2 rounded-md border px-4 text-sm font-medium ${formTramite.esPrioritarioPorDefecto === v ? 'border-brand-600 bg-brand-50 text-brand-800 ring-2 ring-brand-600' : 'border-slate-300 text-slate-600'}`}>
                    <input type="radio" name="prioritario" className="sr-only" checked={formTramite.esPrioritarioPorDefecto === v} onChange={() => setFormTramite({ ...formTramite, esPrioritarioPorDefecto: v })} />
                    {t}
                  </label>
                ))}
              </div>
            </fieldset>
            <fieldset className="space-y-2">
              <legend className="text-sm font-medium text-slate-700">Estado</legend>
              <label className="flex h-11 cursor-pointer items-center gap-2 rounded-md border border-slate-300 px-4 text-sm font-medium text-slate-600">
                <input type="checkbox" className="h-4 w-4 rounded border-slate-300 text-brand-600 focus:ring-brand-600" checked={formTramite.activo} onChange={(e) => setFormTramite({ ...formTramite, activo: e.target.checked })} />
                Activo (los recepcionistas pueden usarlo)
              </label>
            </fieldset>
            <div className="flex justify-end gap-2 border-t border-slate-100 pt-4">
              <Button type="button" variant="outline" onClick={() => setDrawerTramite(null)}>Cancelar</Button>
              <Button type="submit" loading={guardandoTramite}>{drawerTramite.modo === 'crear' ? 'Crear trámite' : 'Guardar cambios'}</Button>
            </div>
          </form>
        </Drawer>
      )}

      {/* Confirmación destructiva */}
      {confirmarEliminar && (
        <ConfirmDialog
          titulo="Eliminar"
          mensaje={`¿Eliminar ${confirmarEliminar.etiqueta}? Esta acción no se puede deshacer.`}
          confirmarTexto="Sí, eliminar"
          cargando={eliminando}
          onConfirmar={eliminarSeleccionado}
          onCancelar={() => setConfirmarEliminar(null)}
        />
      )}
    </div>
  );
}
