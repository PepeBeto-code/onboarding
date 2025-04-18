import { AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { OnboardingService } from '../../services/onboarding.service';
import { CommonModule } from '@angular/common';
import { StepPersonalComponent } from '../../components/step-personal/step-personal.component';
import { Onboardin } from '../../models/user.model';
import { Router } from '@angular/router';
import { StepAddressComponent } from '../../components/step-address/step-address.component';
import { StepDocumentsComponent } from '../../components/step-documents/step-documents.component';
import { LoadingOverlayComponent } from '../../components/loading-overlay/loading-overlay.component';
import { finalize } from 'rxjs/operators';
import { CanComponentDeactivate } from '../../guards/unsaved-changes.guard';

@Component({
  selector: 'app-onboarding',
  imports: [CommonModule, StepPersonalComponent, StepAddressComponent, StepDocumentsComponent, LoadingOverlayComponent],
  templateUrl: './onboarding.component.html',
  styleUrl: './onboarding.component.scss'
})
export class OnboardingComponent implements OnInit, CanComponentDeactivate   {
  @ViewChild(StepPersonalComponent, { static: false }) personalStep!: StepPersonalComponent;
  @ViewChild(StepAddressComponent, { static: false }) addressStep!: StepAddressComponent;
  @ViewChild(StepDocumentsComponent, { static: false }) documentStep!: StepDocumentsComponent;

  currentStep!: number;
  totalSteps = 3;
  isLoading = true; 

  constructor(private onboardingService: OnboardingService, private router: Router, private cd: ChangeDetectorRef
  ) {
    localStorage.setItem('onboarding', 'true');
  }

  ngOnInit() {
    this.onboardingService.getOnboardingUser()
    .pipe(
      finalize(() => {
        this.isLoading = false;
        this.cd.detectChanges();
      })
    )
    .subscribe((data: Onboardin) => {
      console.log(data)
      this.currentStep = data.stepCompleted;
    });
  }

  canDeactivate(): boolean {
    if (this.personalStep?.f?.dirty || this.addressStep?.f?.dirty || this.documentStep?.f?.dirty) {
      return confirm('Tienes cambios sin guardar. ¿Seguro que quieres salir?');
    }
    return true;
  }

  handleLoadingChange(isLoading: boolean) {
    this.isLoading = isLoading;
  }

  on(){
    this.router.navigate(['/home']);
  }

  nextStep() {
    if (this.currentStep < this.totalSteps) {
      this.currentStep++;
      this.saveProgress();
    }
  }

  previousStep() {
    if (this.currentStep > 1) {
      this.currentStep--;
      this.saveProgress();
    }
  }

  finish() {
      localStorage.setItem('onboardingCompleto', 'true');
      this.router.navigate(['/home']);
  }

  private saveProgress() {
    this.onboardingService.saveStepProgress(this.currentStep - 1).subscribe({
      next: () => console.log('Progreso guardado'),
      error: err => console.error('Error guardando progreso', err)
    });
  }
}
