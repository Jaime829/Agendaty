import { Link } from 'react-router-dom';
import {
  Building2,
  ShieldCheck,
  BellRing,
  ArrowRight,
} from 'lucide-react';
import { PublicNav } from '@/components/PublicNav';
import { useAuthContext } from '@/context/AuthContext';
import { getHomeByRole } from '@/utils/roleRoutes';

const roles = [
  {
    titulo: 'Ciudadano',
    descripcion: 'Su turno en pantalla: cuándo lo llaman y en qué estado está.',
  },
  {
    titulo: 'Recepcionista',
    descripcion: 'Registra el trámite y entrega el número de turno al ciudadano.',
  },
  {
    titulo: 'Asesor',
    descripcion: 'Llama al siguiente turno y registra el resultado de cada atención.',
  },
  {
    titulo: 'Coordinador',
    descripcion: 'Cola en vivo y reportes de tiempos de espera.',
  },
  {
    titulo: 'Administrador',
    descripcion: 'Gestiona usuarios, ventanillas y tipos de trámite.',
  },
];

const faqs = [
  {
    pregunta: '¿Cómo obtengo mi número de turno?',
    respuesta:
      'En la entidad, el personal de ventanilla registra tu trámite y el sistema te asigna un número único. Los turnos prioritarios se atienden primero.',
  },
  {
    pregunta: '¿Puedo consultar mi turno desde el celular?',
    respuesta:
      'Sí. Entra con tu cuenta, ve a «Mi turno» y escribe tu número. Verás si está en espera, llamado, en atención, finalizado, cancelado o no atendido.',
  },
  {
    pregunta: '¿Y si no escucho mi llamado?',
    respuesta:
      'Si no respondes al llamado, el turno pasa a «No atendido» y debes solicitar uno nuevo en ventanilla.',
  },
  {
    pregunta: '¿Crear la cuenta tiene algún costo?',
    respuesta:
      'No. Registrarte como ciudadano es gratis. Las cuentas de asesores, recepcionistas, coordinadores y administradores las crea un administrador.',
  },
];

export default function LandingPage() {
  const { user, loading, isAuthenticated } = useAuthContext();
  const panelDeMiRol = getHomeByRole(user?.rol);

  return (
    <div className="min-h-screen">
      <PublicNav />

      {/* Hero */}
      <section className="overflow-hidden bg-gradient-to-b from-brand-50 via-surface to-surface">
        <div className="page-gutter mx-auto grid max-w-7xl grid-cols-1 items-center gap-10 py-14 lg:grid-cols-2 lg:py-20">
          <div className="animate-rise-in">
            <p className="mb-4 inline-flex items-center gap-2 rounded-full bg-white px-3 py-1 text-xs font-semibold text-brand-800 ring-1 ring-inset ring-brand-200">
              <BellRing className="h-3.5 w-3.5" aria-hidden="true" /> Atención al ciudadano por turnos
            </p>
            <h1 className="text-4xl font-extrabold leading-[1.1] tracking-tight text-slate-900 sm:text-5xl">
              Toma tu turno y<br />
              <span className="text-brand-700">llega cuando te toca</span>
            </h1>
            <p className="mt-5 max-w-lg text-lg text-slate-600">
              Regístrate, pide tu número en ventanilla y sigue tu turno en tiempo real: en espera, llamado, en
              atención. Los turnos prioritarios se atienden primero.
            </p>
            <div className="mt-8 flex flex-col gap-3 sm:flex-row">
              {loading ? (
                <div className="h-[52px] w-56 rounded-md bg-slate-200/70" aria-hidden="true" />
              ) : isAuthenticated ? (
                <>
                  <Link
                    to={panelDeMiRol}
                    className="inline-flex h-[52px] items-center justify-center gap-2 rounded-md bg-brand-600 px-6 text-base font-semibold text-white transition-colors hover:bg-brand-700"
                  >
                    Ir a mi panel <ArrowRight className="h-5 w-5" aria-hidden="true" />
                  </Link>
                  <a
                    href="#como-funciona"
                    className="inline-flex h-[52px] items-center justify-center rounded-md border border-slate-300 bg-white px-6 text-base font-semibold text-slate-800 transition-colors hover:bg-slate-50"
                  >
                    Ver cómo funciona
                  </a>
                </>
              ) : (
                <>
                  <Link
                    to="/registro"
                    className="inline-flex h-[52px] items-center justify-center gap-2 rounded-md bg-brand-600 px-6 text-base font-semibold text-white transition-colors hover:bg-brand-700"
                  >
                    Crear mi cuenta <ArrowRight className="h-5 w-5" aria-hidden="true" />
                  </Link>
                  <Link
                    to="/login"
                    className="inline-flex h-[52px] items-center justify-center rounded-md border border-slate-300 bg-white px-6 text-base font-semibold text-slate-800 transition-colors hover:bg-slate-50"
                  >
                    Iniciar sesión
                  </Link>
                </>
              )}
            </div>
            <ul className="mt-8 flex flex-wrap gap-x-6 gap-y-2 text-sm text-slate-600">
              {['Número único por turno', 'Tu turno en pantalla en vivo', 'Los prioritarios atienden primero'].map((t) => (
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
              <p className="mt-4 text-center text-xs text-slate-400">Así se verá tu turno en pantalla</p>
            </div>
          </div>
        </div>
      </section>

      {/* Cómo funciona */}
      <section className="bg-white py-16" aria-labelledby="como-funciona">
        <div className="page-gutter mx-auto max-w-7xl">
          <div className="mx-auto max-w-2xl text-center">
            <h2 id="como-funciona" className="text-2xl font-bold text-slate-900 sm:text-3xl">Cómo funciona</h2>
            <p className="mt-2 text-slate-600">De la puerta a la ventanilla en tres pasos.</p>
          </div>
          <ol className="mt-12 grid gap-8 sm:grid-cols-3">
            {[
              {
                n: 1,
                t: 'Crea tu cuenta',
                d: 'Nombre, correo, documento y teléfono. Tu cuenta queda como ciudadana; solo tú puedes crearla.',
              },
              {
                n: 2,
                t: 'Pide tu turno en ventanilla',
                d: 'El personal registra tu trámite y el sistema te asigna un número único. Los prioritarios van primero.',
              },
              {
                n: 3,
                t: 'Sígule el rastro a tu turno',
                d: 'Consulta tu estado: en espera, llamado, en atención, finalizado, cancelado o no atendido.',
              },
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
            <p className="mt-2 text-slate-600">Cada persona ve exactamente lo que necesita para su trabajo.</p>
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
            <p className="mt-2 text-slate-600">Lo que suele preguntar la gente antes de su turno.</p>
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
          <h2 className="text-3xl font-bold text-white">La próxima vez, llega y te llaman</h2>
          <p className="mx-auto mt-3 max-w-xl text-brand-100">
            Crea tu cuenta hoy. Cuando vayas por tu trámite, llevarás un número en la mano y sabrás cuándo te toca.
          </p>
          {loading ? (
            <div className="mx-auto mt-8 h-[52px] w-48 rounded-md bg-brand-100/60" aria-hidden="true" />
          ) : isAuthenticated ? (
            <Link
              to={panelDeMiRol}
              className="mt-8 inline-flex items-center gap-2 rounded-md bg-white px-8 py-4 text-base font-semibold text-brand-800 transition-colors hover:bg-brand-50"
            >
              Ir a mi panel <ArrowRight className="h-5 w-5" aria-hidden="true" />
            </Link>
          ) : (
            <Link
              to="/registro"
              className="mt-8 inline-flex items-center gap-2 rounded-md bg-white px-8 py-4 text-base font-semibold text-brand-800 transition-colors hover:bg-brand-50"
            >
              Crear mi cuenta <ArrowRight className="h-5 w-5" aria-hidden="true" />
            </Link>
          )}
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-slate-200 bg-white py-8">
        <div className="page-gutter mx-auto flex max-w-7xl flex-col items-center justify-between gap-4 sm:flex-row">
          <div className="flex items-center gap-2">
            <Building2 className="h-5 w-5 text-brand-600" aria-hidden="true" />
            <span className="font-bold text-slate-900">Agendaty</span>
          </div>
          <p className="text-sm text-slate-500">Gestión de turnos y atención al ciudadano.</p>
        </div>
      </footer>
    </div>
  );
}
