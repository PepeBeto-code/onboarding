import { Component, OnInit } from '@angular/core';
import { OnboardingService } from '../../services/onboarding.service';
import { CommonModule } from '@angular/common';
import { StepPersonalComponent } from '../step-personal/step-personal.component';
@Component({
  selector: 'app-onboarding',
  imports: [CommonModule, StepPersonalComponent],
  templateUrl: './onboarding.component.html',
  styleUrl: './onboarding.component.scss',
  standalone: true
})
export class OnboardingComponent implements OnInit {
  currentStep = 1;
  totalSteps = 3;

  constructor(private onboardingService: OnboardingService) {}

  ngOnInit() {
    this.onboardingService.loadExistingData().subscribe(data => {
      if (data.stepCompleted && data.stepCompleted < this.totalSteps) {
        this.currentStep = data.stepCompleted + 1;
      }
    });
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
    this.onboardingService.finalizeOnboarding().subscribe({
      next: () => {
        console.log('Onboarding completado');
        // Puedes redirigir o mostrar confirmación aquí
      },
      error: err => {
        console.error('Error al finalizar el onboarding:', err);
        // Mostrar mensaje de error si deseas
      }
    });
  }

  private saveProgress() {
    this.onboardingService.saveStepProgress(this.currentStep - 1).subscribe({
      next: () => console.log('Progreso guardado'),
      error: err => console.error('Error guardando progreso', err)
    });
  }
}
