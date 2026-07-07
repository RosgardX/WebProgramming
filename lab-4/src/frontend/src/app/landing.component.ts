import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './auth.service';
import { MessageService } from 'primeng/api';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, InputTextModule, ButtonModule, ToastModule],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.scss'],
  providers: [MessageService],
})
export class LandingComponent {
  isRegister = true;
  showPassword = false;

  form = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(64)]],
    password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(128)]],
  });

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private msg: MessageService
  ) {}

  toggle(mode: 'login' | 'register') {
    this.isRegister = mode === 'register';
  }

  toggleShowPassword() {
    this.showPassword = !this.showPassword;
  }

  private showValidationErrors() {
    const u = this.form.controls.username;
    const p = this.form.controls.password;

    if (u.errors?.['required']) {
      this.msg.add({ severity: 'warn', summary: 'Проверьте поля', detail: 'Логин обязателен' });
      return true;
    }
    if (u.errors?.['minlength']) {
      this.msg.add({ severity: 'warn', summary: 'Проверьте поля', detail: 'Слишком короткий логин (мин 3 символа)' });
      return true;
    }
    if (p.errors?.['required']) {
      this.msg.add({ severity: 'warn', summary: 'Проверьте поля', detail: 'Пароль обязателен' });
      return true;
    }
    if (p.errors?.['minlength']) {
      this.msg.add({ severity: 'warn', summary: 'Проверьте поля', detail: 'Слишком короткий пароль (мин 6 символов)' });
      return true;
    }
    return false;
  }

  submit() {
    if (this.form.invalid) {
      if (this.showValidationErrors()) return;
    }
    const { username, password } = this.form.value;
    if (!username || !password) return;

    const action = this.isRegister
      ? this.auth.register(username, password)
      : this.auth.login(username, password);

    action.subscribe({
      next: (resp: any) => {
        if (resp?.token) localStorage.setItem('authToken', resp.token);
        this.router.navigate(['/app']);
      },
      error: (err) => {
        const detail = err?.message || 'Ошибка запроса';
        this.msg.add({ severity: 'error', summary: 'Ошибка', detail });
      },
    });
  }
}
