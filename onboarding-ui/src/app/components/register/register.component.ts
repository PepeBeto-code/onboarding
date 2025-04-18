import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { LoadingOverlayComponent } from '../loading-overlay/loading-overlay.component';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterModule, CommonModule, LoadingOverlayComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  standalone: true
})
export class RegisterComponent {
  registerForm: FormGroup;
  serverError: string | null = null;
  validationErrors: { [key: string]: string } = {};
  public isLoading = false;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.pattern('^(?=.*[a-z])(?=.*[A-Z]).*$')]],
      confirmPassword: ['', []]
    }, {
      validators: this.passwordMatchValidator // Esto asegura que el error 'notMatching' esté disponible en el FormGroup
    });
  }

  // Validación personalizada para verificar que las contraseñas coincidan
  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;
  
    return password === confirmPassword ? null : { notMatching: true };
  }

  onSubmit(): void {
    this.registerForm.markAllAsTouched(); // Marca todos los controles como tocados
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.authService.register(this.registerForm.value)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe({
        next: () => this.router.navigate(['/login']),
        error: err => {
          console.error('Register error:', err);
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
