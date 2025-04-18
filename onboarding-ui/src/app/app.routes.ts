import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { LandingPageComponent } from './pages/landing-page/landing-page.component';
import { HomeComponent } from './pages/home/home.component';
import { AuthGuard } from './guards/auth.guard';
import { AuthRedirectGuard } from './guards/auth-redirect.guard';
import { OnboardingComponent } from './pages/onboarding/onboarding.component';
import { EditProfileComponent } from './pages/edit-profile/edit-profile.component';
import { UnsavedChangesGuard } from './guards/unsaved-changes.guard';

export const routes: Routes = [
    { path: '', redirectTo: 'lading-page', pathMatch: 'full' },
    { path: 'login', component: LoginComponent, canActivate: [AuthRedirectGuard]  },
    { path: 'register', component: RegisterComponent, canActivate: [AuthRedirectGuard]  },
    { path: 'lading-page', component: LandingPageComponent },
    { path: 'onboarding', component: OnboardingComponent,
         canActivate: [AuthGuard] , canDeactivate: [UnsavedChangesGuard]
         },
    { path: 'profile/edit', component: EditProfileComponent
    , canActivate: [AuthGuard], canDeactivate: [UnsavedChangesGuard],
    },
    { path: 'home', component: HomeComponent
        , canActivate: [AuthGuard]
        },
];
