import '@testing-library/jest-dom/vitest';
import { cleanup } from '@testing-library/react';
import { afterEach } from 'vitest';

// Limpia el DOM entre pruebas (equivalente a globals: true de jest pero explícito)
afterEach(() => {
  cleanup();
});
