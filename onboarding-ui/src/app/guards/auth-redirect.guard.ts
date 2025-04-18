import { Inject, Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class AuthRedirectGuard implements CanActivate {
  constructor(private router: Router, @Inject(PLATFORM_ID) private platformId: Object) {}

  canActivate(): boolean {
    const isAuthenticated = this.checkAuth();
    if (isAuthenticated) {
      this.router.navigate(['/home']); // Redirige a "home" si el usuario ya está autenticado
      return false; // Evita acceso a "login" o "register"
    }
    return true; // Permite acceso si no está autenticado
  }

  private checkAuth(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      // Solo accede a localStorage si estamos en el navegador
      return !!localStorage.getItem('token');
    }
    return false; // En el servidor asumimos que no está autenticado
  }
}
