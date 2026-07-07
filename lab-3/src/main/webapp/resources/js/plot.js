(function () {
    const CANVAS_ID = 'plot';
    let lastPoint = null;
    let allPoints = [];

    function getCanvas() {
        const canvas = document.getElementById(CANVAS_ID);
        return canvas && canvas.getContext ? canvas : null;
    }

    function getContext() {
        const canvas = getCanvas();
        return canvas ? canvas.getContext('2d') : null;
    }

    function syncCanvasSize() {
        const canvas = getCanvas();
        if (!canvas) return { w: 0, h: 0 };
        const displayW = canvas.clientWidth || canvas.width;
        const displayH = canvas.clientHeight || canvas.height;
        if (canvas.width !== displayW || canvas.height !== displayH) {
            canvas.width = displayW;
            canvas.height = displayH;
        }
        return { w: canvas.width, h: canvas.height };
    }

    function getR() {
        const rInput = document.getElementById('form:rValue');
        if (!rInput) return null;
        const v = parseFloat(rInput.value);
        return isNaN(v) || v <= 0 ? null : v;
    }

    function clear() {
        const ctx = getContext();
        const canvas = getCanvas();
        if (!ctx || !canvas) return;
        ctx.clearRect(0, 0, canvas.width, canvas.height);
    }

    function drawAxesAndTicks(R) {
        const ctx = getContext(), c = getCanvas();
        if (!ctx || !c) return;
        const w = c.width, h = c.height, cx = w / 2, cy = h / 2;
        const scale = (w / 3) / (R || 1);

        ctx.save();
        ctx.strokeStyle = "#444";
        ctx.fillStyle = "#000";
        ctx.lineWidth = 1.2;
        ctx.font = "13px Arial";

        ctx.beginPath(); ctx.moveTo(0, cy); ctx.lineTo(w, cy); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(cx, 0); ctx.lineTo(cx, h); ctx.stroke();

        function tickX(xVal, label) {
            const x = cx + xVal * scale;
            ctx.beginPath(); ctx.moveTo(x, cy - 6); ctx.lineTo(x, cy + 6); ctx.stroke();
            ctx.fillText(label, x - 10, cy + 20);
        }
        function tickY(yVal, label) {
            const y = cy - yVal * scale;
            ctx.beginPath(); ctx.moveTo(cx - 6, y); ctx.lineTo(cx + 6, y); ctx.stroke();
            ctx.fillText(label, cx + 10, y + 4);
        }

        if (R) {
            tickX(-R, "-R"); tickX(-R / 2, "-R/2"); tickX(R / 2, "R/2"); tickX(R, "R");
            tickY(R, "R"); tickY(R / 2, "R/2"); tickY(-R / 2, "-R/2"); tickY(-R, "-R");
        }
        ctx.fillText("x", w - 14, cy - 8);
        ctx.fillText("y", cx + 8, 14);
        ctx.restore();
    }

    function drawArea(R) {
        const ctx = getContext(), c = getCanvas();
        if (!ctx || !c || !R || R <= 0) return;
        const w = c.width, h = c.height, cx = w / 2, cy = h / 2;
        const scale = (w / 3) / R;
        const m2c = (x, y) => ({ x: cx + x * scale, y: cy - y * scale });

        ctx.save();
        ctx.fillStyle = "rgba(52,152,219,0.45)";

        ctx.beginPath();
        ctx.moveTo(cx, cy);
        ctx.arc(cx, cy, R * scale, -Math.PI / 2, 0, false);
        ctx.closePath();
        ctx.fill();

        ctx.beginPath();
        const p1 = m2c(-R / 2, 0);
        const p2 = m2c(0, 0);
        const p3 = m2c(0, -R);
        const p4 = m2c(-R / 2, -R);
        ctx.moveTo(p1.x, p1.y);
        ctx.lineTo(p2.x, p2.y);
        ctx.lineTo(p3.x, p3.y);
        ctx.lineTo(p4.x, p4.y);
        ctx.closePath();
        ctx.fill();

        ctx.beginPath();
        const t1 = m2c(0, 0);
        const t2 = m2c(R / 2, 0);
        const t3 = m2c(0, -R);
        ctx.moveTo(t1.x, t1.y);
        ctx.lineTo(t2.x, t2.y);
        ctx.lineTo(t3.x, t3.y);
        ctx.closePath();
        ctx.fill();

        ctx.restore();
    }

    function toCanvasCoords(x, y, R) {
        const canvas = getCanvas();
        if (!canvas || !R || R <= 0) return null;
        const w = canvas.width;
        const cx = w / 2;
        const cy = canvas.height / 2;
        const scale = (w / 3) / R;
        return { cx: cx + x * scale, cy: cy - y * scale };
    }

    function toLogicalCoords(px, py, R) {
        const canvas = getCanvas();
        if (!canvas || !R || R <= 0) return null;
        const w = canvas.width;
        const cx = w / 2;
        const cy = canvas.height / 2;
        const scale = (w / 3) / R;
        return { x: (px - cx) / scale, y: (cy - py) / scale };
    }

    function isHit(x, y, R) {
        if (x >= 0 && y >= 0 && (x * x + y * y <= R * R)) return true;
        if (x >= -R / 2 && x <= 0 && y >= -R && y <= 0) return true;
        if (x >= 0 && x <= R / 2 && y >= -R && y <= 0 && y >= 2 * (x-(R/2))) return true;
        return false;
    }

    window.drawPoint = function (x, y, R, hit) {
        const ctx = getContext();
        if (!ctx) return;
        const p = toCanvasCoords(x, y, R);
        if (!p) return;
        ctx.save();
        ctx.fillStyle = (hit === undefined || hit === null) ? "#3498db" : (hit ? "#2ecc71" : "#e74c3c");
        ctx.beginPath();
        ctx.arc(p.cx, p.cy, 4, 0, 2 * Math.PI);
        ctx.fill();
        ctx.restore();
    };

    function drawAllPoints() {
        const R = getR();
        if (!R) return;
        allPoints.forEach(pt => {
            if (pt && typeof pt.r === 'number' && Math.abs(pt.r - R) < 1e-9) {
                drawPoint(pt.x, pt.y, pt.r, pt.hit);
            }
        });
    }

    window.setLastPoint = function (pt) {
        lastPoint = pt || null;
        if (pt && typeof pt.x === 'number' && typeof pt.y === 'number' && typeof pt.r === 'number') {
            const exists = allPoints.some(existing =>
                Math.abs(existing.x - pt.x) < 1e-9 &&
                Math.abs(existing.y - pt.y) < 1e-9 &&
                Math.abs(existing.r - pt.r) < 1e-9
            );
            if (!exists) {
                allPoints.push(pt);
            }
        }
    };

    window.redrawPlot = function () {
        syncCanvasSize();
        clear();
        const R = getR();
        drawArea(R);
        drawAxesAndTicks(R);
        drawAllPoints();
    };

    window.clearPoints = function () {
        allPoints = [];
        lastPoint = null;
    };

    window.onCanvasClickHandler = function (event) {
        const canvas = getCanvas();
        if (!canvas) return;
        syncCanvasSize();
        const rect = canvas.getBoundingClientRect();
        const scaleX = canvas.width / rect.width;
        const scaleY = canvas.height / rect.height;
        const clickX = (event.clientX - rect.left) * scaleX;
        const clickY = (event.clientY - rect.top) * scaleY;

        const R = getR();
        if (R) {
            const lc = toLogicalCoords(clickX, clickY, R);
            if (lc) {
                const hit = isHit(lc.x, lc.y, R);
                setLastPoint({ x: lc.x, y: lc.y, r: R, hit });
                redrawPlot();
            }
        }

        if (typeof rcClick === 'function') {
            rcClick([
                { name: 'canvasX', value: clickX },
                { name: 'canvasY', value: clickY },
                { name: 'canvasW', value: canvas.width },
                { name: 'canvasH', value: canvas.height }
            ]);
        }
    };

    window.drawImmediateFromInputs = function () {
        const xEl = document.getElementById('form:xInput');
        const yEl = document.getElementById('form:yInput');
        const r = getR();
        if (!xEl || !yEl || !r) return;
        const x = parseFloat(xEl.value);
        const y = parseFloat(yEl.value);
        if (!isFinite(x) || !isFinite(y)) return;
        const hit = isHit(x, y, r);
        setLastPoint({ x, y, r, hit });
        redrawPlot();
    };

    window.addEventListener('load', function () {
        if (typeof window.redrawPlot === 'function') {
            window.redrawPlot();
        }
    });
})();