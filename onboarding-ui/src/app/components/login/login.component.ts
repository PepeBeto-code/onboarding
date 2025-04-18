import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { LoadingOverlayComponent } from '../loading-overlay/loading-overlay.component';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterModule, CommonModule, LoadingOverlayComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  standalone: true
})
export class LoginComponent {
  loginForm: FormGroup;
  serverError: string | null = null;
  validationErrors: { [key: string]: string } = {};
  public isLoading = false;

  constructor(private fb: FormBuilder, private router: Router, private authService: AuthService) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required,  Validators.minLength(8), Validators.pattern('^(?=.*[a-z])(?=.*[A-Z]).*$')]]
    });
  }

  onSubmit(): void {
    this.loginForm.markAllAsTouched(); // Marca todos los controles como tocados
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.authService.login(this.loginForm.value)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe({
        next: () => this.router.navigate(['/home']),
        error: err => {
          console.error('Login error:', err);

          if(err.status === 403){
            this.serverError = 'Usuario y/o contraseña incorrectos';
            return;
          }

          if (err.status === 400 && typeof err.error === 'object') {
            // Mostrar errores de validación
            this.validationErrors = err.error; // lo asignas a una variable en el componente
          } else {
            this.serverError = 'Ocurrió un error inesperado.';
          }
        }
      });
    }
  }
}
