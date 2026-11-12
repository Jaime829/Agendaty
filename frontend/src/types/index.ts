export interface Turno {
  id: number;
  numeroTurno: string;
  estado: 'EN_ESPERA' | 'LLAMADO' | 'EN_ATENCION' | 'EN_PAUSA' | 'FINALIZADO' | 'CANCELADO' | 'NO_ATENDIDO';
  prioridad: 'NORMAL' | 'PRIORITARIO';
  fechaCreacion: string;
  fechaLlamado?: string;
  fechaFinEspera?: string;
  ciudadanoId: number;
  ciudadanoNombre?: string;
  tipoTramiteId: number;
  tipoTramiteNombre?: string;
  asesorId?: number;
  asesorNombre?: string;
}

export interface TipoTramite {
  id: number;
  nombre: string;
  codigo: string;
  esPrioritarioPorDefecto: boolean;
  activo: boolean;
}

export interface Atencion {
  id: number;
  turnoId: number;
  numeroTurno: string;
  asesorId: number;
  asesorNombre: string;
  fechaInicio?: string;
  fechaFin?: string;
  observaciones?: string;
  resultado?: string;
  tiempoEsperaMinutos?: number;
  tiempoAtencionMinutos?: number;
  totalPausasMinutos?: number;
}

export interface Asesor {
  id: number;
  nombre: string;
  disponible: boolean;
  numeroVentanilla?: number;
}

export interface AuthUser {
  token: string;
  tipo: string;
  usuarioId: number;
  username: string;
  rol: string;
}

export interface ApiError {
  timeStamp: string;
  status: number;
  errorMessage: string;
  errorsDetails: string;
}

export interface RegisterRequest {
  nombre: string;
  correo: string;
  password: string;
  documento: string;
  telefono: string;
}

export interface LoginRequest {
  correo: string;
  password: string;
}
