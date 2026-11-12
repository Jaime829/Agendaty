import { useEffect, useState } from 'react';
import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
  useLocation,
} from 'react-router-dom';
import { AuthProvider, useAuthContext } from '@/context/AuthContext';
import { Navbar } from '@/components/Navbar';
import { Button } from '@/components/Button';
import LandingPage from '@/pages/LandingPage';
import RegistroPage from '@/pages/RegistroPage';
import LoginPage from '@/pages/LoginPage';
import RecepcionistaPage from '@/pages/RecepcionistaPage';
import AsesorPage from '@/pages/AsesorPage';
import CoordinadorPage from '@/pages/CoordinadorPage';
import AdministradorPage from '@/pages/AdministradorPage';
import CiudadanoPage from '@/pages/CiudadanoPage';
import ReportesPage from '@/pages/ReportesPage';

export default function App() {
  const [inicializado, setInicializado] = useState(false);

  useEffect(() => {
    setInicializado(true);
  }, []);

  if (!inicializado) return null;

  return (
    <AuthProvider>
      <BrowserRouter>
        <div className="min-h-screen bg-slate-50">
          <Navbar />
          <main className="pb-8">
            <Routes>
              <Route path="/" element={<LandingPage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/registro" element={<RegistroPage />} />
              <Route
                path="/recepcionista"
                element={
                  <ProtectedRoute roles={['ROLE_RECEPCIONISTA', 'ROLE_ADMINISTRADOR']}>
                    <RecepcionistaPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/asesor"
                element={
                  <ProtectedRoute roles={['ROLE_ASESOR', 'ROLE_ADMINISTRADOR']}>
                    <AsesorPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/coordinador"
                element={
                  <ProtectedRoute roles={['ROLE_COORDINADOR', 'ROLE_ADMINISTRADOR']}>
                    <CoordinadorPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/administrador"
                element={
                  <ProtectedRoute roles={['ROLE_ADMINISTRADOR']}>
                    <AdministradorPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/ciudadano"
                element={
                  <ProtectedRoute roles={['ROLE_CIUDADANO', 'ROLE_ADMINISTRADOR']}>
                    <CiudadanoPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/reportes"
                element={
                  <ProtectedRoute roles={['ROLE_COORDINADOR', 'ROLE_ADMINISTRADOR']}>
                    <ReportesPage />
                  </ProtectedRoute>
                }
              />
            </Routes>
          </main>
        </div>
      </BrowserRouter>
    </AuthProvider>
  );
}
