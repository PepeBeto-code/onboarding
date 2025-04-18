import { Inject, Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root', // Esto lo registra como un servicio singleton
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router,  @Inject(PLATFORM_ID) private platformId: Object) {}

  canActivate(): boolean {
    const isAuthenticated = this.checkAuth();
    if (!isAuthenticated) {
      this.router.navigate(['/login']); 
      return false;
    }
    return true;
  }

  private checkAuth(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      // Solo accede a localStorage si estamos en el navegador
      return !!localStorage.getItem('token');
    }
    return false; // En el servidor asumimos que no está autenticado
  }
}
