import { Component, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { StepPersonalComponent } from '../../components/step-personal/step-personal.component';
import { StepAddressComponent } from '../../components/step-address/step-address.component';
import { StepDocumentsComponent } from '../../components/step-documents/step-documents.component';
import { throwError } from 'rxjs';
import { Router, RouterModule } from '@angular/router';
import { CanComponentDeactivate } from '../../guards/unsaved-changes.guard';
import { LoadingOverlayComponent } from '../../components/loading-overlay/loading-overlay.component';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule ,StepPersonalComponent, StepAddressComponent, StepDocumentsComponent, LoadingOverlayComponent],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.scss'
})
export class EditProfileComponent implements CanComponentDeactivate {
  @ViewChild(StepPersonalComponent) personalStep!: StepPersonalComponent;
  @ViewChild(StepAddressComponent) addressStep!: StepAddressComponent;
  @ViewChild(StepDocumentsComponent) documentStep!: StepDocumentsComponent;

  personalForm: FormGroup;
  addressForm: FormGroup;
  documentForm: FormGroup;
  disabled: boolean = false;
  isLoading = false; 

  constructor(private fb: FormBuilder, private router: Router) {
    this.personalForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      birthday: ['', Validators.required]
    });

    this.addressForm = this.fb.group({
      street: ['', Validators.required],
      number: ['', Validators.required],
      postalCode: ['', Validators.required],
      city: ['', Validators.required]
    });

    this.documentForm = this.fb.group({
      docType: ['', Validators.required],
      fileBase64: [null, Validators.required],
      fileType: ['', Validators.required],
      fileName: ['', Validators.required]
    });
  }

  canDeactivate(): boolean {
    if (this.personalForm?.dirty || this.addressForm?.dirty || this.documentForm?.dirty) {
      return confirm('Tienes cambios sin guardar. ¿Seguro que quieres salir?');
    }
    return true;
  }

  async guardarCambios() {
    this.personalForm.markAllAsTouched();
    this.addressForm.markAllAsTouched();
    this.documentForm.markAllAsTouched();

    if (this.personalForm.invalid || this.addressForm.invalid || this.documentForm.invalid) {
      const errores: string[] = [];

      if (this.personalForm.invalid) {
        errores.push('Información personal');
        console.log('Errores en personalForm:', this.getFormErrors(this.personalForm));
      }
    
      if (this.addressForm.invalid) {
        errores.push('Dirección');
        console.log('Errores en addressForm:', this.getFormErrors(this.addressForm));
      }
    
      if (this.documentForm.invalid) {
        errores.push('Documentos');
        console.log('Errores en documentForm:', this.getFormErrors(this.documentForm));
      }
    
      console.error(`Formulario inválido en las siguientes secciones: ${errores.join(', ')}`);
      return
    }

    this.isLoading = true;
    try {
      await Promise.all([
        this.personalStep.onNext(),
        this.addressStep.onNext?.(), // por si no implementaste `submit()` en address
        this.documentStep.onSubmit()
      ]);
  
      this.isLoading = false;

      this.personalForm.reset(this.personalForm.value);
      this.addressForm.reset(this.addressForm.value);
      this.documentForm.reset(this.documentForm.value);

      this.router.navigate(['/home']);
    } catch (err) {
      console.error('Error en uno de los formularios:', err);
      // Puedes mostrar mensaje en pantalla también
    }
  }

  handleLoadingChange(isLoading: boolean) {
    this.isLoading = isLoading;
  }

  on(){
    this.router.navigate(['/home']);
  }

  getFormErrors(form: FormGroup): any {
    const errors: any = {};
    Object.keys(form.controls).forEach(key => {
      const controlErrors = form.get(key)?.errors;
      if (controlErrors) {
        errors[key] = controlErrors;
      }
    });
    return errors;
  }
  
}
