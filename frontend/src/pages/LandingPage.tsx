import { Link } from 'react-router-dom';
import {
  Building2,
  ShieldCheck,
  BellRing,
  ArrowRight,
} from 'lucide-react';

const roles = [
  {
    titulo: 'Ciudadano',
    descripcion: 'Consulta el estado de tu turno y cuánto falta para que te atiendan.',
  },
  {
    titulo: 'Recepcionista',
    descripcion: 'Registra el trámite y genera el número de turno desde la ventanilla.',
  },
  {
    titulo: 'Asesor',
    descripcion: 'Llama al siguiente turno, atiende y finaliza con resultado registrado.',
  },
  {
    titulo: 'Coordinador',
    descripcion: 'Ve la cola en vivo y los reportes de tiempos de espera.',
  },
  {
    titulo: 'Administrador',
    descripcion: 'Crea usuarios, gestiona ventanillas y tipos de trámite.',
  },
];

const faqs = [
  {
    pregunta: '¿Cómo obtengo mi número de turno?',
    respuesta:
      'Una vez en la entidad, el personal de ventanilla registra tu trámite y el sistema genera un número de turno único. Los turnos prioritarios pasan primero (RN3).',
  },
  {
    pregunta: '¿Qué significa "En espera", "Llamado", "En atención"?',
    respuesta:
      'En espera: tu turno está en la cola. Llamado: te están llamando en este momento. En atención: un asesor ya te está atendiendo. También puedes ver "Finalizado", "Cancelado", "No atendido" y "En pausa".',
  },
  {
    pregunta: '¿Qué pasa si no escucho mi llamado?',
    respuesta:
      'Si no respondes al llamado, el turno pasa a estado "No atendido" (RN2) y tendrás que volver a solicitar turno. Mantente atento a las pantallas o a tu teléfono.',
  },
  {
    pregunta: '¿Quiénes pueden registrar usuarios internos?',
    respuesta:
      'Solo un administrador puede crear asesores, recepcionistas, coordinadores y otros administradores, y también ciudadanos manualmente. El registro público solo crea cuentas de ciudadano.',
  },
];

export default function LandingPage() {
  return (
    <div className="min-h-screen">
      {/* Barra pública */}
      <header className="border-b border-slate-200 bg-white/90 backdrop-blur">
        <div className="page-gutter mx-auto flex h-16 max-w-7xl items-center justify-between">
          <Link to="/" className="flex items-center gap-2" aria-label="Agendaty — inicio">
            <Building2 className="h-6 w-6 text-brand-600" aria-hidden="true" />
            <span className="text-lg font-bold tracking-tight text-slate-900">Agendaty</span>
          </Link>
          <nav className="flex items-center gap-2 sm:gap-4" aria-label="Navegación pública">
            <Link to="/login" className="rounded-md px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100">
              Iniciar sesión
            </Link>
            <Link
              to="/registro"
              className="rounded-md bg-brand-600 px-4 py-2 text-sm font-semibold text-white hover:bg-brand-700"
            >
              Crear cuenta
            </Link>
          </nav>
        </div>
      </header>

      {/* Hero */}
      <section className="overflow-hidden bg-gradient-to-b from-brand-50 via-surface to-surface">
        <div className="page-gutter mx-auto grid max-w-7xl grid-cols-1 items-center gap-10 py-14 lg:grid-cols-2 lg:py-20">
          <div className="animate-rise-in">
            <p className="mb-4 inline-flex items-center gap-2 rounded-full bg-white px-3 py-1 text-xs font-semibold text-brand-800 ring-1 ring-inset ring-brand-200">
              <BellRing className="h-3.5 w-3.5" aria-hidden="true" /> Turnos con orden y trazabilidad
            </p>
            <h1 className="text-4xl font-extrabold leading-[1.1] tracking-tight text-slate-900 sm:text-5xl">
              Atención al ciudadano,<br />
              <span className="text-brand-700">sin esperas innecesarias</span>
            </h1>
            <p className="mt-5 max-w-lg text-lg text-slate-600">
              Regístrate, toma tu número de turno y conoce tu posición en la cola en tiempo real. Prioridad para
              quien la necesita y registro completo de cada atención.
            </p>
            <div className="mt-8 flex flex-col gap-3 sm:flex-row">
              <Link
                to="/registro"
                className="inline-flex h-[52px] items-center justify-center gap-2 rounded-md bg-brand-600 px-6 py-4 text-base font-semibold text-white transition-colors hover:bg-brand-700"
              >
                Registrarse como ciudadano <ArrowRight className="h-5 w-5" aria-hidden="true" />
              </Link>
              <Link
                to="/login"
                className="inline-flex h-[52px] items-center justify-center rounded-md border border-slate-300 bg-white px-6 py-4 text-base font-semibold text-slate-800 transition-colors hover:bg-slate-50"
              >
                Ya tengo cuenta
              </Link>
            </div>
            <ul className="mt-8 flex flex-wrap gap-x-6 gap-y-2 text-sm text-slate-600">
              {['Número único por turno', 'Prioridad protocolizada', 'Sin listas manuales'].map((t) => (
                <li key={t} className="flex items-center gap-2">
                  <ShieldCheck className="h-4 w-4 text-green-600" aria-hidden="true" />
                  {t}
                </li>
              ))}
            </ul>
          </div>

          {/* Visual de la cola (decorativo) */}
          <div className="mx-auto w-full max-w-md animate-rise-in" aria-hidden="true">
            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-pop">
              <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                <p className="text-xs font-semibold uppercase tracking-widest text-slate-400">Cola de atención</p>
                <span className="relative flex h-2.5 w-2.5">
                  <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-green-400 opacity-75" />
                  <span className="relative inline-flex h-2.5 w-2.5 rounded-full bg-green-500" />
                </span>
              </div>
              <div className="mt-6 text-center">
                <p className="text-xs font-medium uppercase tracking-wide text-slate-500">Ahora en ventanilla</p>
                <p className="mt-2 font-mono text-6xl font-extrabold tracking-tight text-brand-800">CEDN-00042</p>
                <p className="mt-3 inline-flex items-center gap-1.5 rounded-full bg-indigo-50 px-3 py-1 text-xs font-semibold text-indigo-800">
                  <span className="h-1.5 w-1.5 rounded-full bg-indigo-600" /> En atención
                </p>
              </div>
              <ul className="mt-6 space-y-2 border-t border-slate-100 pt-4">
                {[
                  { n: 'PAS-00013', d: 'Pasaporte', p: 'Prioritario' },
                  { n: 'CED-00041', d: 'Cédula de identidad', p: null },
                  { n: 'LIC-00007', d: 'Licencia de conducción', p: null },
                ].map(({ n, d, p }) => (
                  <li key={n} className="flex items-center justify-between rounded-lg bg-slate-50 px-4 py-2">
                    <span className="font-mono text-sm font-semibold text-slate-800">{n}</span>
                    <span className="flex items-center gap-2 text-xs text-slate-500">
                      {d}
                      {p && (
                        <span className="rounded-full bg-amber-100 px-2 py-0.5 font-semibold text-amber-800">{p}</span>
                      )}
                    </span>
                  </li>
                ))}
              </ul>
              <p className="mt-4 text-center text-xs text-slate-400">Visual ilustrativo</p>
            </div>
          </div>
        </div>
      </section>

      {/* Cómo funciona */}
      <section className="bg-white py-16" aria-labelledby="como-funciona">
        <div className="page-gutter mx-auto max-w-7xl">
          <div className="mx-auto max-w-2xl text-center">
            <h2 id="como-funciona" className="text-2xl font-bold text-slate-900 sm:text-3xl">Cómo funciona</h2>
            <p className="mt-2 text-slate-600">Tres pasos para una atención ordenada.</p>
          </div>
          <ol className="mt-12 grid gap-8 sm:grid-cols-3">
            {[
              { n: 1, t: 'Regístrate', d: 'Crea tu cuenta de ciudadano con tus datos. Solo tú puedes hacerlo; toma un minuto.' },
              { n: 2, t: 'Toma tu turno', d: 'El personal de ventanilla registra tu trámite y el sistema asigna un número único. Los prioritarios pasan primero.' },
              { n: 3, t: 'Espera y sé atendido', d: 'Consulta tu posición en la cola y el estado de tu turno cuando lo necesites.' },
            ].map(({ n, t, d }) => (
              <li key={n} className="rounded-xl border border-slate-200 p-6">
                <span className="flex h-10 w-10 items-center justify-center rounded-full bg-brand-600 text-base font-bold text-white">
                  {n}
                </span>
                <h3 className="mt-4 font-bold text-slate-900">{t}</h3>
                <p className="mt-1 text-sm text-slate-600">{d}</p>
              </li>
            ))}
          </ol>
        </div>
      </section>

      {/* Roles */}
      <section className="bg-surface py-16" aria-labelledby="roles">
        <div className="page-gutter mx-auto max-w-7xl">
          <div className="mx-auto max-w-2xl text-center">
            <h2 id="roles" className="text-2xl font-bold text-slate-900 sm:text-3xl">Un sistema para cada rol</h2>
            <p className="mt-2 text-slate-600">Cada persona ve exactamente lo que necesita.</p>
          </div>
          <div className="mt-12 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-5">
            {roles.map(({ titulo, descripcion }) => (
              <div key={titulo} className="rounded-xl border border-slate-200 bg-white p-5">
                <h3 className="font-bold text-slate-900">{titulo}</h3>
                <p className="mt-1 text-sm text-slate-600">{descripcion}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* FAQ */}
      <section className="bg-white py-16" aria-labelledby="faq">
        <div className="page-gutter mx-auto max-w-4xl">
          <div className="mx-auto max-w-2xl text-center">
            <h2 id="faq" className="text-2xl font-bold text-slate-900 sm:text-3xl">Preguntas frecuentes</h2>
            <p className="mt-2 text-slate-600">Lo que suelen preguntarnos antes de la primera visita.</p>
          </div>
          <div className="mt-10 divide-y divide-slate-200 rounded-xl border border-slate-200">
            {faqs.map(({ pregunta, respuesta }) => (
              <details key={pregunta} className="group px-6 py-4">
                <summary className="flex cursor-pointer list-none items-center justify-between gap-4 font-semibold text-slate-900 [&::-webkit-details-marker]:hidden">
                  {pregunta}
                  <span className="text-slate-400 transition-transform group-open:rotate-45" aria-hidden="true">+</span>
                </summary>
                <p className="mt-3 text-sm leading-relaxed text-slate-600">{respuesta}</p>
              </details>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="bg-brand-800 py-16">
        <div className="page-gutter mx-auto max-w-4xl text-center">
          <h2 className="text-3xl font-bold text-white">Tu turno, en orden</h2>
          <p className="mx-auto mt-3 max-w-xl text-brand-100">
            Regístrate gratis, evita las filas y llega a tu trámite sabiendo exactamente cuándo te atenderán.
          </p>
          <Link
            to="/registro"
            className="mt-8 inline-flex items-center gap-2 rounded-md bg-white px-8 py-4 text-base font-semibold text-brand-800 transition-colors hover:bg-brand-50"
          >
            Crear mi cuenta <ArrowRight className="h-5 w-5" aria-hidden="true" />
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-slate-200 bg-white py-8">
        <div className="page-gutter mx-auto flex max-w-7xl flex-col items-center justify-between gap-4 sm:flex-row">
          <div className="flex items-center gap-2">
            <Building2 className="h-5 w-5 text-brand-600" aria-hidden="true" />
            <span className="font-bold text-slate-900">Agendaty</span>
          </div>
          <p className="text-sm text-slate-500">Plataforma de gestión de turnos y atención al ciudadano.</p>
        </div>
      </footer>
    </div>
  );
}

