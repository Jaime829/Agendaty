import { describe, it, expect, beforeEach } from 'vitest';
import { authService } from '@/api/auth';

describe('authService.getUser', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('retorna null y limpia localStorage cuando el JSON está corrupto', () => {
    localStorage.setItem('user', '{esto no es json');
    localStorage.setItem('token', 'abc');
    expect(authService.getUser()).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('retorna null y limpia cuando el objeto no tiene rol', () => {
    localStorage.setItem('user', JSON.stringify({ usuarioId: 1, username: 'x@y.com' }));
    localStorage.setItem('token', 'abc');
    expect(authService.getUser()).toBeNull();
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('retorna el objeto cuando la sesión es válida', () => {
    const user = { usuarioId: 3, username: 'a@b.com', rol: 'ROLE_ASESOR' };
    localStorage.setItem('user', JSON.stringify(user));
    localStorage.setItem('token', 'abc');
    expect(authService.getUser()).toEqual(user);
  });
});
