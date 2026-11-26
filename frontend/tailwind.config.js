/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        // Marca Agendaty — azul institucional profundo (GOV.UK-style sobrio, identidad propia)
        brand: {
          50: '#eef4fa',
          100: '#dce8f5',
          200: '#b9d2ea',
          300: '#8fb8dd',
          400: '#5f9cd0',
          500: '#3a7fc0',
          600: '#175b9c',
          700: '#124a80',
          800: '#0f3a63',
          900: '#0c2c4a',
        },
        // Superficie de la app (ligeramente más fresca que slate-50)
        surface: '#f6f8fb',
        // Estados de turno (mismos hex usados en los badges semánticos)
        estado: {
          espera: { bg: '#fdf0d5', text: '#8a5a00', dot: '#d97706' },
          llamado: { bg: '#e3edfb', text: '#1e4d8f', dot: '#1d6fd6' },
          atencion: { bg: '#e6e6fb', text: '#3b3b8f', dot: '#4f46e5' },
          pausa: { bg: '#eceef1', text: '#4b5563', dot: '#6b7280' },
          finalizado: { bg: '#e3f7ec', text: '#166534', dot: '#16a34a' },
          cancelado: { bg: '#eceef1', text: '#6b7280', dot: '#9ca3af' },
          noatendido: { bg: '#fde8e8', text: '#991b1b', dot: '#dc2626' },
        },
      },
      fontFamily: {
        sans: ['"Inter Variable"', 'Inter', 'ui-sans-serif', 'system-ui', '-apple-system', '"Segoe UI"', 'sans-serif'],
        mono: ['ui-monospace', 'SFMono-Regular', 'Menlo', 'Consolas', 'monospace'],
      },
      borderRadius: {
        sm: '8px',
        md: '10px',
        lg: '12px',
        xl: '16px',
      },
      boxShadow: {
        card: '0 1px 2px 0 rgb(15 23 42 / 0.05), 0 4px 16px -4px rgb(15 23 42 / 0.08)',
        pop: '0 8px 32px -8px rgb(15 23 42 / 0.28)',
      },
      zIndex: {
        dropdown: '30',
        sticky: '20',
        modal: '50',
        toast: '60',
      },
      transitionTimingFunction: {
        out: 'cubic-bezier(0.16, 1, 0.3, 1)',
        in: 'cubic-bezier(0.7, 0, 0.84, 0)',
      },
      transitionDuration: {
        160: '160ms',
        220: '220ms',
        320: '320ms',
      },
      keyframes: {
        'fade-in': {
          from: { opacity: '0' },
          to: { opacity: '1' },
        },
        'rise-in': {
          from: { opacity: '0', transform: 'translateY(10px)' },
          to: { opacity: '1', transform: 'translateY(0)' },
        },
        'pulse-dot': {
          '0%, 100%': { opacity: '1' },
          '50%': { opacity: '0.4' },
        },
      },
      animation: {
        'fade-in': 'fade-in 220ms cubic-bezier(0.16, 1, 0.3, 1) both',
        'rise-in': 'rise-in 320ms cubic-bezier(0.16, 1, 0.3, 1) both',
        'pulse-dot': 'pulse-dot 1.6s ease-in-out infinite',
      },
    },
  },
  plugins: [],
};
