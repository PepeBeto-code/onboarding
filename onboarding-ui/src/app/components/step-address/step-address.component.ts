import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { OnboardingService } from '../../services/onboarding.service';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize, firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-step-address',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './step-address.component.html',
  styleUrl: './step-address.component.scss'
})
export class StepAddressComponent {
  @Output() next = new EventEmitter<void>();
  @Output() prev = new EventEmitter<void>();
  @Input() form!: FormGroup;
  @Input() modo: 'onboarding' | 'edicion' = 'onboarding';
  serverError: string | null = null;
  validationErrors: { [key: string]: string } = {};
  @Output() loadingChange = new EventEmitter<boolean>();

  constructor(private fb: FormBuilder, private onboardingService: OnboardingService) {}

  get f() {
    return this.form;
  }

  ngOnInit() {
    if(this.modo === 'onboarding'){
      this.form = this.fb.group({
        street: ['', Validators.required],
        number: ['', Validators.required],
        postalCode: ['', Validators.required],
        city: ['', Validators.required]
      });
    }
  
      this.onboardingService.getAddressInfo()
      .pipe(
        finalize(() => {
          this.loadingChange.emit(false);
        })
      )
      .subscribe({
        next: (data) => {
          if (data) {
            this.form.patchValue(data);
          }
        },
        error: err => {
          console.warn('No se pudo cargar address info:', err);
        }
      });
  }

  onPrev(){
    if(this.form.dirty){
      const resultado =  confirm('Tienes cambios sin guardar. ¿Seguro que quieres salir?');
      if (resultado) {
        this.prev.emit()
      }
      return;
    }
    this.prev.emit()
  }

  async onNext(): Promise<void> {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.loadingChange.emit(true);
      try {
        await firstValueFrom(this.onboardingService.saveAddressInfo(this.form.value));
        this.next.emit(); // Emitir el evento después de que la información haya sido guardada exitosamente.
      } catch (err: unknown) {
        console.error('Error al guardar dirección:', err);
        // Verificación de tipo del error antes de acceder a sus propiedades
        if (err instanceof HttpErrorResponse) {
          if (err.status === 400 && typeof err.error === 'object') {
            this.validationErrors = err.error; // Manejo de errores de validación.
          } else {
            this.serverError = 'Ocurrió un error inesperado.'; // Error general.
          }
        } else {
          this.serverError = 'Ocurrió un error inesperado.'; // Error desconocido.
        }
      }
      this.loadingChange.emit(false);
    }
  }
  
  
}
