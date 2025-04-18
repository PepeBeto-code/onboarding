import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Personal } from '../models/user.model';

@Injectable({
   providedIn: 'root', 
  })
export class OnboardingService {

  private apiUrl = '/api/onboarding';

  constructor(private http: HttpClient) {}

  savePersonalInfo(data: Personal): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/step/personal`, data);
  }

  getPersonalInfo(): Observable<any> {
    return this.http.get( `${this.apiUrl}/step/personal`);
  }  

  getOnboardingUser(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}`);
  }

  loadExistingData(): Observable<{ stepCompleted: number }> {
    // Simular petición al backend
    return of({ stepCompleted: 0 }); // Simulamos que no ha completado pasos
  }

  uploadDocument(docType: string, file: any, fileType: any, fileName: any): Observable<any> {
    console.log("se envia",  {
      docType: docType,
      fileType: fileType,
      fileName: fileName,
      fileBase64: file  // data:image/png;base64,...
    })
    return this.http.post(`/api/onboarding/step/documents`, {
      docType: docType,
      fileType: fileType,
      fileName: fileName,
      fileBase64: file  // data:image/png;base64,...
    });
  }

  getUploadedDocuments(): Observable<any[]> {
    return this.http.get<any[]>('/api/onboarding/step/documents');
  }  
  
  saveAddressInfo(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/step/address`, data);
  }  

  getAddressInfo(): Observable<any> {
    return this.http.get(`${this.apiUrl}/step/address`);
  }
  

  saveStepProgress(step: number): Observable<void> {
    console.log('Guardando progreso en el paso:', step);
    return of(); // Aquí deberías hacer una petición real al backend
  }

  finalizeOnboarding(): Observable<void> {
    console.log('Finalizando onboarding...');
    return of(); // Aquí deberías hacer una petición real al backend
  }
}
