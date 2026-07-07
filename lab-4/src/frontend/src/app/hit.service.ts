import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Hit } from './hit.model';

@Injectable({ providedIn: 'root' })
export class HitService {
  private base = '/api/hits'; // через proxy пойдёт на http://localhost:8080/api/hits

  constructor(private http: HttpClient) {}

  addHit(payload: { x: number; y: number; r: number }): Observable<Hit> {
    return this.http.post<Hit>(this.base, payload);
  }

  getHits(): Observable<Hit[]> {
    return this.http.get<Hit[]>(this.base);
  }

  clearHits(): Observable<void> {
    return this.http.delete<void>(this.base);
  }
}
