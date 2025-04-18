import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, HostListener, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { OnboardingService } from '../../services/onboarding.service';
import { CanComponentDeactivate } from '../../guards/unsaved-changes.guard';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-step-personal',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './step-personal.component.html',
  styleUrl: './step-personal.component.scss',
  standalone: true
})
export class StepPersonalComponent implements CanComponentDeactivate  {
  @Output() next = new EventEmitter<void>();
  @Input() form!: FormGroup;
  @Input() modo: 'onboarding' | 'edicion' = 'onboarding';
  serverError: string | null = null;
  validationErrors: { [key: string]: string } = {};
  formChanged: boolean = false; // Indica si el formulario ha cambiado
  @Output() loadingChange = new EventEmitter<boolean>();

  constructor(private fb: FormBuilder, private onboardingService: OnboardingService ){}

  get f() {
    return this.form;
  }
  
  ngOnInit() {
    if(this.modo === 'onboarding'){
      this.form = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        phone: ['', Validators.required],
        birthday: ['', Validators.required]
      });
    }

      this.onboardingService.getPersonalInfo()
      .subscribe({
        next: (data) => this.form.patchValue(data),
        error: err => console.warn('No se pudo cargar la información personal:', err)
      });
  }

  canDeactivate(): boolean {
    if (this.form?.dirty) {
      return confirm('Tienes cambios sin guardar. ¿Seguro que quieres salir?');
    }
    return false;
  }

  onNext(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.form.markAllAsTouched();
      if (this.form.valid) {
        this.loadingChange.emit(true);
        this.formChanged = false; 
        this.onboardingService.savePersonalInfo(this.form.value)
        .pipe(
          finalize(() => {
            this.loadingChange.emit(false);
          })
        )
        .subscribe({
          next: () => {
            this.next.emit();
            resolve();
          },
          error: err => {
            console.error('Register error:', err);
            if (err.status === 400 && typeof err.error === 'object') {
              this.validationErrors = err.error;
            } else {
              this.serverError = 'Ocurrió un error inesperado.';
            }
            reject(err);
          }, 
        });
      } else {
        reject('Formulario personal inválido');
      }
    });
  }
  

  
}
