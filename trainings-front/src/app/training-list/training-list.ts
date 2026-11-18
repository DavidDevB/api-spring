import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Training } from '../models/training';
import { TrainingService } from '../services/training.service';

@Component({
  selector: 'app-training-list',
  imports: [RouterLink, CurrencyPipe],
  templateUrl: './training-list.html',
  styleUrl: './training-list.css',
})
export class TrainingList implements OnInit {
  private readonly trainingService = inject(TrainingService);

  protected readonly trainings = signal<Training[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.trainingService.getAll().subscribe({
      next: (data) => {
        this.trainings.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set("Impossible de charger les formations. L'API Spring est-elle démarrée ?");
        this.loading.set(false);
      },
    });
  }

  remove(training: Training): void {
    if (!training.id || !confirm(`Supprimer la formation « ${training.name} » ?`)) {
      return;
    }
    this.trainingService.delete(training.id).subscribe({
      next: () => this.trainings.update((list) => list.filter((t) => t.id !== training.id)),
      error: () => this.error.set('La suppression a échoué.'),
    });
  }
}
