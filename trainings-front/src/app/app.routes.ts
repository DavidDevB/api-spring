import { Routes } from '@angular/router';
import { TrainingForm } from './training-form/training-form';
import { TrainingList } from './training-list/training-list';

export const routes: Routes = [
  { path: '', redirectTo: 'trainings', pathMatch: 'full' },
  { path: 'trainings', component: TrainingList },
  { path: 'trainings/new', component: TrainingForm },
  { path: 'trainings/:id/edit', component: TrainingForm },
  { path: '**', redirectTo: 'trainings' }
];
