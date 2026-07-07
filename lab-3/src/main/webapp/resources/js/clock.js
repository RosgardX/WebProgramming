(function () {
    function getCanvas() {
        return document.getElementById('analogClock');
    }

    function drawClock(ctx, radius) {
        drawFace(ctx, radius);
        drawNumbers(ctx, radius);
        drawTime(ctx, radius);
    }

    function drawFace(ctx, radius) {
        var grad;
        ctx.beginPath();
        ctx.arc(0, 0, radius, 0, 2 * Math.PI);
        ctx.fillStyle = 'white';
        ctx.fill();

        grad = ctx.createRadialGradient(0, 0, radius * 0.95, 0, 0, radius * 1.05);
        grad.addColorStop(0, '#333');
        grad.addColorStop(0.5, 'white');
        grad.addColorStop(1, '#333');
        ctx.strokeStyle = grad;
        ctx.lineWidth = radius * 0.1;
        ctx.stroke();

        ctx.beginPath();
        ctx.arc(0, 0, radius * 0.05, 0, 2 * Math.PI);
        ctx.fillStyle = '#333';
        ctx.fill();
    }

    function drawNumbers(ctx, radius) {
        var ang, num;
        ctx.font = radius * 0.15 + "px Arial";
        ctx.textBaseline = "middle";
        ctx.textAlign = "center";
        for (num = 1; num <= 12; num++) {
            ang = num * Math.PI / 6;
            ctx.rotate(ang);
            ctx.translate(0, -radius * 0.85);
            ctx.rotate(-ang);
            ctx.fillText(num.toString(), 0, 0);
            ctx.rotate(ang);
            ctx.translate(0, radius * 0.85);
            ctx.rotate(-ang);
        }
    }

    function drawTime(ctx, radius) {
        var now = new Date();
        var hour = now.getHours();
        var minute = now.getMinutes();
        var second = now.getSeconds();

        // hour
        hour = hour % 12;
        hour = (hour * Math.PI / 6) +
            (minute * Math.PI / (6 * 60)) +
            (second * Math.PI / (360 * 60));
        drawHand(ctx, hour, radius * 0.5, radius * 0.07);

        // minute
        var minuteAngle = (minute * Math.PI / 30) + (second * Math.PI / (30 * 60));
        drawHand(ctx, minuteAngle, radius * 0.8, radius * 0.07);

        // second (красная)
        var secondAngle = second * Math.PI / 30;
        drawHand(ctx, secondAngle, radius * 0.9, radius * 0.02, "#e74c3c");
    }

    function drawHand(ctx, pos, length, width, color) {
        ctx.beginPath();
        ctx.lineWidth = width;
        ctx.lineCap = "round";
        ctx.strokeStyle = color || '#333';
        ctx.moveTo(0, 0);
        ctx.rotate(pos);
        ctx.lineTo(0, -length);
        ctx.stroke();
        ctx.rotate(-pos);
    }

    function initClock() {
        var canvas = getCanvas();
        if (!canvas || !canvas.getContext) {
            return;
        }
        var ctx = canvas.getContext("2d");
        var radius = canvas.height / 2;
        ctx.translate(radius, radius);
        radius = radius * 0.90;

        function tick() {
            ctx.clearRect(-canvas.width, -canvas.height, canvas.width * 2, canvas.height * 2);
            drawClock(ctx, radius);
        }

        tick();
        setInterval(tick, 1000);
    }

    window.addEventListener('load', initClock);
})();