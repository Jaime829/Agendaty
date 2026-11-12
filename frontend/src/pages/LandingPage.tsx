import { Link } from 'react-router-dom';
import { Building2, User, Clock, Users, LayoutDashboard, ShieldCheck, Zap } from 'lucide-react';
import { Button } from '@/components/Button';

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-slate-50">
      {/* Navbar */}
      <nav className="bg-white border-b border-slate-200 sticky top-0 z-20">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Building2 className="h-7 w-7 text-blue-600" />
              <span className="text-xl font-bold text-slate-900 tracking-tight">Agendaty</span>
            </div>
            <div className="flex items-center gap-4">
              <Link to="/login" className="text-sm font-medium text-slate-600 hover:text-blue-600 transition-colors">
                Iniciar sesión
              </Link>
              <Link to="/registro" className="px-4 py-2 bg-blue-600 text-white rounded-md text-sm font-medium hover:bg-blue-700 transition-colors">
                Registrarse
              </Link>
            </div>
          </div>
        </div>
      </nav>

      {/* Hero */}
      <header className="bg-white border-b border-slate-200 py-12 px-4">
        <div className="max-w-7xl mx-auto text-center">
          <h1 className="text-4xl font-bold text-slate-900 mb-6">
            Atención al ciudadano,<br />
            sin esperas innecesarias
          </h1>
          <p className="text-lg text-slate-600 mb-8 max-w-2xl mx-auto">
            Plataforma de gestión de turnos que organiza la atención por orden de llegada o prioridad,
            reduce la congestión y brinda trazabilidad completa de cada turno.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link to="/registro" className="flex-1 px-6 py-4 bg-blue-600 text-white rounded-xl text-lg font-medium hover:bg-blue-700 transition-colors">
              Registrarse como ciudadano
            </Link>
            <Link to="/login" className="flex-1 px-6 py-4 bg-white text-slate-900 border border-slate-200 rounded-xl text-lg font-medium hover:bg-slate-50 transition-colors">
              Iniciar sesión
            </Link>
          </div>
        </div>
      </header>

      {/* Cómo funciona */}
      <section className="py-16 px-4 bg-slate-50">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-12">
            <h2 className="text-2xl font-bold text-slate-900 mb-3">Cómo funciona</h2>
            <p className="text-slate-600">Tres pasos para una atención organizada</p>
          </div>
          <div className="grid md:grid-cols-3 gap-8">
            <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
              <div className="w-12 h-12 bg-blue-100 text-blue-600 rounded-lg flex items-center justify-center mb-4">
                <User className="h-6 w-6" />
              </div>
              <h3 className="font-bold text-slate-900 mb-2">1. Regístrate</h3>
              <p className="text-slate-600 text-sm">
                Crea tu cuenta como ciudadano en pocos pasos. Siempre tendrás rol de ciudadano.
              </p>
            </div>
            <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
              <div className="w-12 h-12 bg-blue-100 text-blue-600 rounded-lg flex items-center justify-center mb-4">
                <Clock className="h-6 w-6" />
              </div>
              <h3 className="font-bold text-slate-900 mb-2">2. Solicita turno</h3>
              <p className="text-slate-600 text-sm">
                Elige el tipo de trámite y recibe un número de turno único. Los turnos prioritarios pasan primero.
              </p>
            </div>
            <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
              <div className="w-12 h-12 bg-blue-100 text-blue-600 rounded-lg flex items-center justify-center mb-4">
                <ShieldCheck className="h-6 w-6" />
              </div>
              <h3 className="font-bold text-slate-900 mb-2">3. Sé atendido</h3>
              <p className="text-slate-600 text-sm">
                Consulta tu posición en cola y el tiempo estimado de espera en tiempo real.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Beneficios */}
      <section className="py-16 px-4 bg-white">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-12">
            <h2 className="text-2xl font-bold text-slate-900 mb-3">Beneficios para todos</h2>
            <p className="text-slate-600">Una plataforma diseñada para cada rol de la atención</p>
          </div>
          <div className="grid md:grid-cols-5 gap-6">
            <div className="text-center">
              <div className="w-10 h-10 bg-green-100 text-green-600 rounded-lg flex items-center justify-center mx-auto mb-3">
                <Zap className="h-6 w-6" />
              </div>
              <h4 className="font-bold text-slate-900 mb-2">Menos esperas</h4>
              <p className="text-sm text-slate-600">Turnos organizados por prioridad y llegada</p>
            </div>
            <div className="text-center">
              <div className="w-10 h-10 bg-green-100 text-green-600 rounded-lg flex items-center justify-center mx-auto mb-3">
                <Users className="h-6 w-6" />
              </div>
              <h4 className="font-bold text-slate-900 mb-2">Trazabilidad</h4>
              <p className="text-sm text-slate-600">Cada atención queda registrada con resultado</p>
            </div>
            <div className="text-center">
              <div className="w-10 h-10 bg-green-100 text-green-600 rounded-lg flex items-center justify-center mx-auto mb-3">
                <LayoutDashboard className="h-6 w-6" />
              </div>
              <h4 className="font-bold text-slate-900 mb-2">Reportes</h4>
              <p className="text-sm text-slate-600">Tiempo promedio de espera y trámites frecuentes</p>
            </div>
            <div className="text-center">
              <div className="w-10 h-10 bg-green-100 text-green-600 rounded-lg flex items-center justify-center mx-auto mb-3">
                <Building2 className="h-6 w-6" />
              </div>
              <h4 className="font-bold text-slate-900 mb-2">Ventanillas</h4>
              <p className="text-sm text-slate-600">Gestión de asesores y disponibilidad</p>
            </div>
            <div className="text-center">
              <div className="w-10 h-10 bg-green-100 text-green-600 rounded-lg flex items-center justify-center mx-auto mb-3">
                <ShieldCheck className="h-6 w-6" />
              </div>
              <h4 className="font-bold text-slate-900 mb-2">Seguro</h4>
              <p className="text-sm text-slate-600">Contraseñas protegidas con BCrypt</p>
            </div>
          </div>
        </div>
      </section>

      {/* Tipos de usuario */}
      <section className="py-16 px-4 bg-slate-50">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-12">
            <h2 className="text-2xl font-bold text-slate-900 mb-3">Roles del sistema</h2>
            <p className="text-slate-600">Cada usuario tiene su vista y sus permisos</p>
          </div>
          <div className="grid md:grid-cols-5 gap-6">
            <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
              <h4 className="font-bold text-slate-900 mb-2">Ciudadano</h4>
              <p className="text-sm text-slate-600">Consulta estado de su turno y tiempo de espera</p>
            </div>
            <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
              <h4 className="font-bold text-slate-900 mb-2">Recepcionista</h4>
              <p className="text-sm text-slate-600">Genera turnos y gestiona la cola</p>
            </div>
            <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
              <h4 className="font-bold text-slate-900 mb-2">Asesor</h4>
              <p className="text-sm text-slate-600">Atiende turnos, inicia y finaliza atención</p>
            </div>
            <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
              <h4 className="font-bold text-slate-900 mb-2">Coordinador</h4>
              <p className="text-sm text-slate-600">Monitor de cola y reportes en tiempo real</p>
            </div>
            <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
              <h4 className="font-bold text-slate-900 mb-2">Administrador</h4>
              <p className="text-sm text-slate-600">Gestiona usuarios, ventanillas y trámites</p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16 px-4 bg-blue-600">
        <div className="max-w-7xl mx-auto text-center">
          <h2 className="text-3xl font-bold text-white mb-4">¿Listo para organizar la atención?</h2>
          <p className="text-blue-100 mb-8 max-w-2xl mx-auto">
            Regístrate hoy y comienza a gestionar turnos de forma eficiente.
          </p>
          <Link to="/registro" className="px-8 py-3 bg-white text-blue-600 rounded-xl text-lg font-medium hover:bg-slate-100 transition-colors">
            Crear cuenta gratis
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-white border-t border-slate-200 py-8 px-4">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center gap-4">
          <div className="flex items-center gap-2">
            <Building2 className="h-6 w-6 text-blue-600" />
            <span className="font-bold text-slate-900">Agendaty</span>
          </div>
          <p className="text-sm text-slate-500">
            Plataforma de gestión de turnos y atención al ciudadano.
          </p>
        </div>
      </footer>
    </div>
  );
}
