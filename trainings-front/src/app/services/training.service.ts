import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Training } from '../models/training';

@Injectable({ providedIn: 'root' })
export class TrainingService {
  private readonly http = inject(HttpClient);
  // Relative URL: in dev, proxy.conf.json forwards /api to http://localhost:8080
  private readonly apiUrl = '/api/trainings';

  getAll(): Observable<Training[]> {
    return this.http.get<Training[]>(this.apiUrl);
  }

  getById(id: number): Observable<Training> {
    return this.http.get<Training>(`${this.apiUrl}/${id}`);
  }

  create(training: Training): Observable<Training> {
    return this.http.post<Training>(this.apiUrl, training);
  }

  update(id: number, training: Training): Observable<Training> {
    return this.http.put<Training>(`${this.apiUrl}/${id}`, training);
  }

  delete(id: number): Observable<boolean> {
    return this.http.delete<boolean>(`${this.apiUrl}/${id}`);
  }
}
