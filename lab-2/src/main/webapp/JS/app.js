// Простой JS без var и без сложных конструкций
(() => {
    'use strict';

    const $ = (s, r) => (r || document).querySelector(s);
    const $$ = (s, r) => Array.from((r || document).querySelectorAll(s));

    document.addEventListener('DOMContentLoaded', () => {
        initSingleSelect('xChoice');
        initSingleSelect('r');
        restoreInputs();
        initValidation();
        initCanvas();
    });

    function initSingleSelect(name) {
        const boxes = $$(`input[name="${name}"]`);
        boxes.forEach(b => {
            b.addEventListener('change', () => {
                if (!b.checked) return;
                boxes.forEach(o => { if (o !== b) o.checked = false; });
                if (name === 'r') sessionStorage.setItem('lastR', b.value);
                if (name === 'xChoice') sessionStorage.setItem('lastX', b.value);
                clearError(name === 'xChoice' ? 'x' : 'r');
            });
        });
    }

    function restoreInputs() {
        const lastR = sessionStorage.getItem('lastR');
        if (lastR) {
            const rb = $(`input[name="r"][value="${lastR}"]`);
            if (rb) { $$('input[name="r"]').forEach(x => x.checked = false); rb.checked = true; }
        }
        const lastX = sessionStorage.getItem('lastX');
        if (lastX) {
            const xb = $(`input[name="xChoice"][value="${lastX}"]`);
            if (xb) { $$('input[name="xChoice"]').forEach(x => x.checked = false); xb.checked = true; }
        }

        const xh = $('#xHidden');
        if (xh) xh.value = '';

        const saved = sessionStorage.getItem('lastPoint');
        if (saved) {
            try {
                const p = JSON.parse(saved);
                const yi = $('#yInput');
                if (yi && Number.isFinite(p?.y)) yi.value = String(p.y);

                const allowedX = [-3,-2,-1,0,1,2,3,4,5];
                if (Number.isFinite(p?.x)) {
                    const xi = Math.round(p.x);
                    if (Math.abs(p.x - xi) < 1e-9 && allowedX.includes(xi)) {
                        const xb = $(`input[name="xChoice"][value="${String(xi)}"]`);
                        if (xb) {
                            $$('input[name="xChoice"]').forEach(el => el.checked = false);
                            xb.checked = true;
                            sessionStorage.setItem('lastX', String(xi));
                        }
                    }
                }
            } catch {}
        }
    }

    function setClientTime() {
        const now = new Date();
        const m = $('#clientTimeMillis'); const t = $('#clientTimeText');
        if (m) m.value = String(now.getTime());
        if (t) t.value = now.toLocaleString();
    }

    function setError(field, msg) {
        const el = document.getElementById(field + 'Error');
        if (el) el.textContent = msg || '';
    }
    function clearError(field) { setError(field, ''); }

    function initValidation() {
        const form = $('#point-form');
        const yi = $('#yInput'); const xh = $('#xHidden');

        if (yi) {
            yi.addEventListener('input', () => clearError('y'));
            yi.addEventListener('blur', () => { if (yi.value) yi.value = yi.value.trim().replace(',', '.'); });
        }
        $$('input[name="xChoice"]').forEach(b => b.addEventListener('change', () => clearError('x')));
        $$('input[name="r"]').forEach(b => b.addEventListener('change', () => clearError('r')));

        form.addEventListener('submit', (e) => {
            let ok = true;

            const rChecked = $$('input[name="r"]:checked');
            const xChoice = $$('input[name="xChoice"]:checked');
            const yRaw = yi && yi.value ? yi.value.trim().replace(',', '.') : '';
            let xRaw = xh && xh.value ? xh.value.trim().replace(',', '.') : '';

            clearError('x'); clearError('y'); clearError('r');

            if (!xRaw) {
                if (xChoice.length !== 1) { setError('x', 'Выберите X или кликните по графику.'); ok = false; }
                else { xRaw = xChoice[0].value; if (xh) xh.value = xRaw; }
            } else {
                const xNum = Number(xRaw);
                if (!Number.isFinite(xNum)) { setError('x', 'X должен быть числом.'); ok = false; }
                else xh.value = String(xNum);
            }

            const yNum = Number(yRaw);
            if (!Number.isFinite(yNum)) { setError('y', 'Введите корректное Y.'); ok = false; }
            else if (!(yNum > -5 && yNum < 5)) { setError('y', 'Y в интервале (-5; 5).'); ok = false; }
            else yi.value = String(yNum);

            if (rChecked.length !== 1) { setError('r', 'Выберите R.'); ok = false; }

            if (!ok) e.preventDefault();
            else setClientTime();
        });
    }

    function initCanvas() {
        const canvas = $('#areaCanvas');
        const form = $('#point-form');
        if (!canvas || !form) return;

        const ctx = canvas.getContext('2d');
        const W = canvas.width, H = canvas.height;
        const CX = Math.floor(W / 2), CY = Math.floor(H / 2);
        const MARGIN = 30;

        const getSelectedR = () => {
            const r = $('input[name="r"]:checked');
            if (!r) return null;
            const v = parseFloat(String(r.value).replace(',', '.'));
            return Number.isFinite(v) ? v : null;
        };
        const getR = () => {
            const sel = getSelectedR();
            if (sel != null) return sel;
            const saved = sessionStorage.getItem('lastR');
            const v = saved ? parseFloat(saved) : NaN;
            return Number.isFinite(v) ? v : 3;
        };
        const getScale = (R) => Math.min((W - 2 * MARGIN) / (2 * R), (H - 2 * MARGIN) / (2 * R));

        const clear = () => { ctx.clearRect(0, 0, W, H); ctx.fillStyle = '#ffffff'; ctx.fillRect(0, 0, W, H); };

        const drawAxes = (R) => {
            const k = getScale(R);
            ctx.strokeStyle = '#888'; ctx.lineWidth = 1;

            ctx.beginPath(); ctx.moveTo(MARGIN, CY); ctx.lineTo(W - MARGIN, CY); ctx.stroke();
            ctx.beginPath(); ctx.moveTo(CX, H - MARGIN); ctx.lineTo(CX, MARGIN); ctx.stroke();

            ctx.beginPath();
            ctx.moveTo(W - MARGIN - 5, CY - 4); ctx.lineTo(W - MARGIN, CY); ctx.lineTo(W - MARGIN - 5, CY + 4);
            ctx.moveTo(CX - 4, MARGIN + 5); ctx.lineTo(CX, MARGIN); ctx.lineTo(CX + 4, MARGIN + 5);
            ctx.stroke();

            ctx.fillStyle = '#555'; ctx.font = '12px sans-serif';
            ctx.fillText('x', W - MARGIN - 12, CY - 6);
            ctx.fillText('y', CX + 6, MARGIN + 12);

            const ticks = [-R, -R / 2, R / 2, R];
            ctx.fillStyle = '#666';

            ticks.forEach(v => {
                const x = CX + v * k;
                ctx.beginPath(); ctx.moveTo(x, CY - 4); ctx.lineTo(x, CY + 4); ctx.stroke();
                const label = v === -R ? '-R' : v === -R / 2 ? '-R/2' : v === R / 2 ? 'R/2' : 'R';
                const tw = ctx.measureText(label).width;
                ctx.fillText(label, x - tw / 2, CY - 8);
            });
            ticks.forEach(v => {
                const y = CY - v * k;
                ctx.beginPath(); ctx.moveTo(CX - 4, y); ctx.lineTo(CX + 4, y); ctx.stroke();
                const label = v === -R ? '-R' : v === -R / 2 ? '-R/2' : v === R / 2 ? 'R/2' : 'R';
                ctx.fillText(label, CX + 6, y - 6);
            });
        };

        const drawArea = (R) => {
            const k = getScale(R);
            ctx.fillStyle = 'rgba(91,192,222,0.25)';
            ctx.strokeStyle = 'rgba(91,192,222,0.7)'; ctx.lineWidth = 1;

            const left = CX - k * R, top = CY;
            ctx.beginPath(); ctx.rect(left, top, k * R, k * R); ctx.fill(); ctx.stroke();

            ctx.beginPath(); ctx.moveTo(CX, CY); ctx.lineTo(CX - k * R, CY); ctx.lineTo(CX, CY - k * R);
            ctx.closePath(); ctx.fill(); ctx.stroke();

            ctx.beginPath(); ctx.moveTo(CX, CY); ctx.arc(CX, CY, k * (R / 2), 0, Math.PI / 2);
            ctx.closePath(); ctx.fill(); ctx.stroke();
        };

        const drawPoint = (x, y, R) => {
            const k = getScale(R);
            const px = CX + x * k, py = CY - y * k;
            ctx.fillStyle = '#d9534f';
            ctx.beginPath(); ctx.arc(px, py, 3.5, 0, Math.PI * 2); ctx.fill();
        };

        const redraw = () => {
            const R = getR();
            clear(); drawArea(R); drawAxes(R);
            const saved = sessionStorage.getItem('lastPoint');
            if (saved) {
                try {
                    const p = JSON.parse(saved);
                    if (Number.isFinite(p?.x) && Number.isFinite(p?.y)) drawPoint(p.x, p.y, R);
                } catch {}
            }
        };

        $$('input[name="r"]').forEach(r => r.addEventListener('change', () => { sessionStorage.setItem('lastR', r.value); redraw(); }));

        canvas.addEventListener('click', (ev) => {
            const rect = canvas.getBoundingClientRect();
            // УЧЁТ CSS-МАСШТАБА: переводим CSS-пиксели -> пиксели canvas
            const scaleX = canvas.width / rect.width;
            const scaleY = canvas.height / rect.height;
            const px = (ev.clientX - rect.left) * scaleX;
            const py = (ev.clientY - rect.top)  * scaleY;

            const sel = getSelectedR();
            const R = sel != null ? sel : getR();
            if (sel == null) {
                const rb = $(`input[name="r"][value="${String(R)}"]`);
                if (rb) { $$('input[name="r"]').forEach(b => b.checked = false); rb.checked = true; sessionStorage.setItem('lastR', String(R)); clearError('r'); }
            }

            const k = getScale(R);
            const x = +(((px - CX) / k).toFixed(3));
            const y = +(((CY - py) / k).toFixed(3));

            $$('input[name="xChoice"]').forEach(b => b.checked = false);
            const xh = $('#xHidden'); const yi = $('#yInput');
            if (xh) xh.value = String(x);
            if (yi) yi.value = String(Math.max(-4.999, Math.min(4.999, y)));

            sessionStorage.setItem('lastX', '');
            sessionStorage.setItem('lastPoint', JSON.stringify({ x, y, r: R }));
            clearError('x'); clearError('y');

            setClientTime();
            redraw();
            form.submit();
        });

        redraw();
    }
})();