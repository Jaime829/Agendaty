import { lazy, Suspense } from 'react';
import {
  BrowserRouter,
  Routes,
  Route,
} from 'react-router-dom';
import { Toaster } from 'sonner';
import { AuthProvider } from '@/context/AuthContext';
import { Navbar } from '@/components/Navbar';
import { Loading } from '@/components/Loading';
import LandingPage from '@/pages/LandingPage';
import RegistroPage from '@/pages/RegistroPage';
import LoginPage from '@/pages/LoginPage';
import { ProtectedRoute } from '@/routes/ProtectedRoute';

// Code splitting por ruta: cada pantalla se descarga bajo demanda
const RecepcionistaPage = lazy(() => import('@/pages/RecepcionistaPage'));
const AsesorPage = lazy(() => import('@/pages/AsesorPage'));
const CoordinadorPage = lazy(() => import('@/pages/CoordinadorPage'));
const AdministradorPage = lazy(() => import('@/pages/AdministradorPage'));
const CiudadanoPage = lazy(() => import('@/pages/CiudadanoPage'));
const ReportesPage = lazy(() => import('@/pages/ReportesPage'));

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <a href="#contenido" className="skip-link">
          Saltar al contenido principal
        </a>
        <div className="min-h-screen bg-surface">
          <Navbar />
          <main id="contenido" className="pb-8">
            <Suspense fallback={<Loading etiqueta="Cargando…" />}>
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
            </Suspense>
          </main>
        </div>
        <Toaster position="top-center" richColors closeButton />
      </BrowserRouter>
    </AuthProvider>
  );
}
