import { useState, useEffect } from 'react';
import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
  useLocation,
} from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { Navbar } from '@/components/Navbar';
import { Button } from '@/components/Button';
import LoginPage from '@/pages/LoginPage';
import RecepcionistaPage from '@/pages/RecepcionistaPage';
import AsesorPage from '@/pages/AsesorPage';
import CoordinadorPage from '@/pages/CoordinadorPage';
import AdministradorPage from '@/pages/AdministradorPage';
import CiudadanoPage from '@/pages/CiudadanoPage';
import ReportesPage from '@/pages/ReportesPage';

function PaginaProtegida({ children }: { children: React.ReactNode }) {
  const { user, loading, login, logout } = useAuth();
  const location = useLocation();

  useEffect(() => {
    if (!loading && !user) {
      navigateToLogin();
    }
  }, [user, loading]);

  const navigateToLogin = () => {
    // no-op: el login se gestiona en App
  };

  if (loading) return null;
  if (!user) return <Navigate to="/login" state={{ from: location }} replace />;

  return <>{children}</>;
}

export default function App() {
  const [inicializado, setInicializado] = useState(false);

  useEffect(() => {
    setInicializado(true);
  }, []);

  if (!inicializado) return null;

  return (
    <BrowserRouter>
      <div className="min-h-screen bg-slate-50">
        <Navbar />
        <main className="pb-8">
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route
              path="/"
              element={
                <PaginaProtegida>
                  <div className="p-6">
                    <h1 className="text-2xl font-bold text-slate-900 mb-6">Panel de control</h1>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                      <Button
                        onClick={() => (window.location.href = '/recepcionista')}
                        className="w-full justify-start px-6"
                      >
                        Ir a Recepcionista
                      </Button>
                      <Button
                        variant="secondary"
                        onClick={() => (window.location.href = '/asesor')}
                        className="w-full justify-start px-6"
                      >
                        Ir a Asesor
                      </Button>
                      <Button
                        variant="secondary"
                        onClick={() => (window.location.href = '/coordinador')}
                        className="w-full justify-start px-6"
                      >
                        Ir a Coordinador
                      </Button>
                      <Button
                        variant="secondary"
                        onClick={() => (window.location.href = '/administrador')}
                        className="w-full justify-start px-6"
                      >
                        Ir a Administrador
                      </Button>
                      <Button
                        variant="secondary"
                        onClick={() => (window.location.href = '/ciudadano')}
                        className="w-full justify-start px-6"
                      >
                        Ir a Ciudadano
                      </Button>
                      <Button
                        variant="secondary"
                        onClick={() => (window.location.href = '/reportes')}
                        className="w-full justify-start px-6"
                      >
                        Ir a Reportes
                      </Button>
                    </div>
                  </div>
                </PaginaProtegida>
              }
            />
            <Route path="/recepcionista" element={<RecepcionistaPage />} />
            <Route path="/asesor" element={<AsesorPage />} />
            <Route path="/coordinador" element={<CoordinadorPage />} />
            <Route path="/administrador" element={<AdministradorPage />} />
            <Route path="/ciudadano" element={<CiudadanoPage />} />
            <Route path="/reportes" element={<ReportesPage />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}
