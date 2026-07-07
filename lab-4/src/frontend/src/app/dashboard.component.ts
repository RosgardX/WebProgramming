import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule, NgClass } from '@angular/common';

import { HitService } from './hit.service';
import { Hit } from './hit.model';
import { MessageService } from 'primeng/api';

import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { ToastModule } from 'primeng/toast';
import { ChartModule, UIChart } from 'primeng/chart';
import { CheckboxModule } from 'primeng/checkbox';
import { SliderModule } from 'primeng/slider';
import { DropdownModule } from 'primeng/dropdown';

import Chart from 'chart.js/auto';

const areaPlugin = {
  id: 'areaPlugin',
  beforeDraw(chart: any) {
    const { ctx, scales, options } = chart;
    const r = options.plugins?.areaPlugin?.r ?? 1;
    const r2 = r / 2;
    const x = scales.x;
    const y = scales.y;

    ctx.save();
    ctx.fillStyle = 'rgba(59,130,246,0.35)';
    ctx.strokeStyle = 'rgba(59,130,246,1)';
    ctx.lineWidth = 2;

    // треугольник (II)
    ctx.beginPath();
    ctx.moveTo(x.getPixelForValue(-r2), y.getPixelForValue(0));
    ctx.lineTo(x.getPixelForValue(0), y.getPixelForValue(r2));
    ctx.lineTo(x.getPixelForValue(0), y.getPixelForValue(0));
    ctx.closePath();
    ctx.fill(); ctx.stroke();

    // прямоугольник (III)
    ctx.beginPath();
    ctx.rect(
      x.getPixelForValue(-r),
      y.getPixelForValue(0),
      x.getPixelForValue(0) - x.getPixelForValue(-r),
      y.getPixelForValue(-r2) - y.getPixelForValue(0)
    );
    ctx.fill(); ctx.stroke();

    // четверть круга (IV), радиус r/2
    ctx.beginPath();
    ctx.moveTo(x.getPixelForValue(0), y.getPixelForValue(0));
    for (let deg = 0; deg >= -90; deg -= 2) {
      const t = deg * Math.PI / 180;
      const px = r2 * Math.cos(t);
      const py = r2 * Math.sin(t);
      ctx.lineTo(x.getPixelForValue(px), y.getPixelForValue(py));
    }
    ctx.closePath();
    ctx.fill(); ctx.stroke();
    ctx.restore();
  }
};
Chart.register(areaPlugin);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, FormsModule, ReactiveFormsModule,
    CardModule, ButtonModule, TableModule, ToastModule,
    ChartModule, CheckboxModule, SliderModule, DropdownModule, NgClass
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  providers: [MessageService],
})
export class DashboardComponent implements OnInit {
  @ViewChild('chartRef') chartComponent!: UIChart;

  xValues = [-4, -3, -2, -1, 0, 1, 2, 3, 4];
  rValues = [1, 2, 3, 4];
  hits: Hit[] = [];

  chartData: any = { datasets: [] };
  chartOptions: any = {
    responsive: true,
    aspectRatio: 1,
    scales: {
      x: { min: -5, max: 5, title: { display: true, text: 'X' } },
      y: { min: -5, max: 5, title: { display: true, text: 'Y' } },
    },
    plugins: {
      legend: {
        position: 'bottom',
        labels: { filter: (item: any) => item.text === 'Попадание' || item.text === 'Промах' }
      },
      tooltip: {
        callbacks: {
          label: (ctx: any) =>
            (ctx.dataset.label === 'Попадание' || ctx.dataset.label === 'Промах')
              ? `(${ctx.raw.x}, ${ctx.raw.y})`
              : ''
        }
      },
      areaPlugin: { r: 1 }
    }
  };

  form = this.fb.group({
    x: [0, Validators.required],
    y: [0, [Validators.required, Validators.min(-3), Validators.max(5)]],
    r: [1, Validators.required],
  });

  constructor(
    private fb: FormBuilder,
    private hitsService: HitService,
    private msg: MessageService,
    private router: Router
  ) {}

  ngOnInit() { this.loadHits(); }

  get xOptions() { return this.xValues.map(v => ({ label: v.toString(), value: v })); }
  get rOptions() { return this.rValues.map(v => ({ label: v.toString(), value: v })); }

  loadHits() {
    this.hitsService.getHits().subscribe({
      next: data => { this.hits = data; this.updateChart(); },
      error: err => this.msg.add({ severity: 'error', summary: 'Ошибка', detail: err.message })
    });
  }

  submit() {
    if (this.form.invalid) {
      this.msg.add({ severity: 'warn', summary: 'Проверьте поля' });
      return;
    }
    const { x, y, r } = this.form.value;
    const xNum = Number(x);
    const yNum = Number(y);
    const rNum = Number(r);

    if (isNaN(xNum) || xNum < -4 || xNum > 4) {
      this.msg.add({ severity: 'error', summary: 'Ошибка', detail: 'X вне области допустимых значений [-4; 4]' });
      return;
    }
    if (isNaN(yNum) || yNum < -3 || yNum > 5) {
      this.msg.add({ severity: 'error', summary: 'Ошибка', detail: 'Y вне области допустимых значений [-3; 5]' });
      return;
    }
    if (isNaN(rNum) || rNum < 1 || rNum > 4) {
      this.msg.add({ severity: 'error', summary: 'Ошибка', detail: 'R должен быть в [1; 4]' });
      return;
    }

    this.hitsService.addHit({ x: xNum, y: yNum, r: rNum }).subscribe({
      next: () => this.loadHits(),
      error: err => this.msg.add({ severity: 'error', summary: 'Ошибка', detail: err.message })
    });
  }

  onCanvasClick(event: MouseEvent, chartRef: UIChart) {
    const chart = chartRef?.chart;
    if (!chart || !(event.target instanceof HTMLCanvasElement)) return;

    const r = this.form.value.r ?? 1;

    const rect = chart.canvas.getBoundingClientRect();
    const xPixel = event.clientX - rect.left;
    const yPixel = event.clientY - rect.top;

    const chartArea = chart.chartArea;
    if (!chartArea) return;
    if (
      xPixel < chartArea.left || xPixel > chartArea.right ||
      yPixel < chartArea.top  || yPixel > chartArea.bottom
    ) {
      return;
    }

    const xScale = chart.scales['x'];
    const yScale = chart.scales['y'];
    if (!xScale || !yScale) return;

    const xValue = xScale.getValueForPixel(xPixel);
    const yValue = yScale.getValueForPixel(yPixel);
    if (xValue === undefined || yValue === undefined) return;

    const roundedX = Number(xValue.toFixed(2));
    const roundedY = Number(yValue.toFixed(2));

    if (roundedX < -4 || roundedX > 4) {
      this.msg.add({ severity: 'error', summary: 'Ошибка', detail: 'X вне области допустимых значений [-4; 4]' });
      return;
    }
    if (roundedY < -3 || roundedY > 5) {
      this.msg.add({ severity: 'error', summary: 'Ошибка', detail: 'Y вне области допустимых значений [-3; 5]' });
      return;
    }

    this.hitsService.addHit({ x: roundedX, y: roundedY, r: Number(r) }).subscribe({
      next: () => this.loadHits(),
      error: err => this.msg.add({ severity: 'error', summary: 'Ошибка', detail: err.message })
    });
  }

  logout() {
    localStorage.removeItem('authToken');
    this.router.navigate(['/']);
  }

  protected updateChart() {
    const r = this.form.value.r ?? 1;

    const axisX = [{ x: -5, y: 0 }, { x: 5, y: 0 }];
    const axisY = [{ x: 0, y: -5 }, { x: 0, y: 5 }];

    const hitsByR = this.hits.filter(h => Number(h['r']) === r);

    this.chartData = {
      datasets: [
        { label: 'Ось', type: 'line', data: axisX, parsing: false, borderColor: '#6b7280', pointRadius: 0 },
        { label: 'Ось', type: 'line', data: axisY, parsing: false, borderColor: '#6b7280', pointRadius: 0 },
        { label: 'Попадание', type: 'scatter', data: hitsByR.filter(h => h['hit']).map(h => ({ x: h['x'], y: h['y'] })), pointRadius: 6, pointBackgroundColor: '#22c55e' },
        { label: 'Промах', type: 'scatter', data: hitsByR.filter(h => !h['hit']).map(h => ({ x: h['x'], y: h['y'] })), pointRadius: 6, pointBackgroundColor: '#ef4444' }
      ]
    };

    this.chartOptions.plugins.areaPlugin.r = r;
    if (this.chartComponent?.chart) {
      this.chartComponent.chart.update();
    }
  }
}
