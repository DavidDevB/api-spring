import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Training } from '../models/training';
import { TrainingService } from '../services/training.service';

@Component({
  selector: 'app-training-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './training-form.html',
  styleUrl: './training-form.css'
})
export class TrainingForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly trainingService = inject(TrainingService);

  protected readonly trainingId = signal<number | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly saving = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    description: [''],
    price: [0, [Validators.required, Validators.min(0)]],
    quantity: [0, [Validators.required, Validators.min(0)]]
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.trainingId.set(Number(id));
      this.trainingService.getById(Number(id)).subscribe({
        next: (training) => {
          if (training) {
            this.form.patchValue(training);
          } else {
            this.error.set('Formation introuvable.');
          }
        },
        error: () => this.error.set('Impossible de charger la formation.')
      });
    }
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const training: Training = this.form.getRawValue();
    const id = this.trainingId();
    const request = id ? this.trainingService.update(id, training) : this.trainingService.create(training);

    this.saving.set(true);
    request.subscribe({
      next: () => this.router.navigate(['/trainings']),
      error: () => {
        this.error.set("L'enregistrement a échoué.");
        this.saving.set(false);
      }
    });
  }
}
