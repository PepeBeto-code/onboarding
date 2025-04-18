import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { OnboardingService } from '../../services/onboarding.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { finalize, firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-step-documents',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './step-documents.component.html',
  styleUrl: './step-documents.component.scss'
})
export class StepDocumentsComponent {
  @Output() prev = new EventEmitter<void>();
  @Output() finish = new EventEmitter<void>();
  @Input() form!: FormGroup;
  @Input() modo: 'onboarding' | 'edicion' = 'onboarding';
  fileToUpload: File | null = null;
  uploadSuccess = false;
  uploadError: string | null = null;
  uploadedDoc: { docType: string; filePath: string; uploadedAt: string; } | undefined;
  previewUrl: SafeResourceUrl | any | null = null;
  fileType: string | null = null;
  fileName: string | null = null;
  @Output() loadingChange = new EventEmitter<boolean>();

  constructor(
    private fb: FormBuilder,
    private onboardingService: OnboardingService,
    private sanitizer: DomSanitizer,
  ) {}

  get f() {
    return this.form;
  }

  ngOnInit() {
    if(this.modo === 'onboarding'){
      this.form = this.fb.group({
        docType: ['', Validators.required],
        fileBase64: [null, Validators.required],
        fileType: ['', Validators.required],
        fileName: ['', Validators.required]
      });
    }

    this.loadUploadedDocuments();
  }

  loadUploadedDocuments() {
    this.onboardingService.getUploadedDocuments()
    .subscribe({
      next: (doc: any) => {
        console.log(doc)
        if (doc) {
          this.fileType = doc?.fileType;
          this.fileName = doc?.fileName;
  
          const base64 = doc?.fileBase64;
          const dataUrl = `data:${this.fileType};base64,${base64}`;
  
          this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(dataUrl);
          console.log(this.previewUrl);
          console.log('Base64 length:', base64?.length);
  
          this.form.patchValue({
            docType: doc?.docType || '',
            fileBase64: dataUrl,
            fileType: doc?.fileType,
            fileName: doc?.fileName
          });
        }
    },
      error: err => {
        console.warn('No se pudieron cargar los documentos:', err);
      }
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.fileToUpload = input.files[0];
      this.fileName = this.fileToUpload.name;
      const reader = new FileReader();
      reader.onload = () => {
        const fileB64 = reader.result as string;

        this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(fileB64);
  
        this.form.patchValue({ fileBase64: reader.result });
      };

      reader.readAsDataURL(this.fileToUpload);

      // Limpiar previa anterior
      if (this.previewUrl) {
        URL.revokeObjectURL(this.previewUrl);
      }

      this.form.patchValue({
        fileType: this.fileToUpload.type,
        fileName: this.fileToUpload.name
       });

      this.fileType = this.fileToUpload.type; // <-- detectar tipo MIME
    }
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

  async onSubmit(): Promise<void> {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.loadingChange.emit(true);
      console.log('Form values before submit:', this.form.value); // Verifica los valores del formulario

      try {
        await firstValueFrom(this.onboardingService.uploadDocument(
          this.form.value.docType,
          this.form.value.fileBase64,
          this.form.value.fileType,
          this.form.value.fileName
        ));

        this.form.reset(this.form.value);
  
        this.uploadSuccess = true;
        this.finish.emit();
      } catch (err) {
        console.error(err);
        this.uploadError = 'Hubo un problema al subir el documento.';
      }
      this.loadingChange.emit(false);
    } else {
      this.uploadError = 'Formulario de documento inválido';
    }
  }
  
}
