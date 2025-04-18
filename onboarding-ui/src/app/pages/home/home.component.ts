import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  nombreUsuario: string = '';
  onboardingCompleto: boolean;
  onboardingIniciado: boolean;
  constructor(){
    this.onboardingIniciado = localStorage.getItem('onboarding') === 'true' || false;
    this.onboardingCompleto = localStorage.getItem('onboardingCompleto') === 'true' || false;
    this.nombreUsuario = localStorage.getItem('name') || 'Usuario';

    console.log(this.onboardingCompleto)
  }

  ngOnInit() {

  }

  logout() {
    localStorage.clear();
    // Redirigir al login
    window.location.href = '/login'; // o usa router.navigate
  }
}
