import { Routes } from '@angular/router';
import { LandingComponent } from './landing.component';
import { DashboardComponent } from './dashboard.component';
import { authGuard } from './auth.guard';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'app', component: DashboardComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
