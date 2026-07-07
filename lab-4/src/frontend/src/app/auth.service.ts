import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(private http: HttpClient) {}

  register(username: string, password: string) {
    return this.http.post('/api/auth/register', { username, password }).pipe(
      catchError((err) => {
        const msg =
          err?.error?.error // текст из нашего бэка
          || err?.error?.message
          || 'Ошибка запроса';
        return throwError(() => new Error(msg));
      })
    );
  }

  login(username: string, password: string) {
    return this.http.post('/api/auth/login', { username, password }).pipe(
      catchError((err) => {
        const msg =
          err?.error?.error
          || err?.error?.message
          || 'Ошибка запроса';
        return throwError(() => new Error(msg));
      })
    );
  }
}
