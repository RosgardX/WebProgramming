package io.github.rosgard.lab2.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@WebFilter(
        filterName = "LimitFilter",
        urlPatterns = {"/*"},
        initParams = {
                @WebInitParam(name = "maxRequests", value = "3"),
                @WebInitParam(name = "windowMillis", value = "5000"),
                @WebInitParam(name = "cleanupIntervalSeconds", value = "30"),
                @WebInitParam(name = "errorImage", value = "/images/img.png")
        }
)
public class LimitFilter implements Filter {

    public static final String CONTEXT_ATTR = "ipRequestCounts";

    private int maxRequests = 3;
    private long windowMillis = 5_000L;
    private long cleanupIntervalSeconds = 30L;
    private String errorImageUrl = "";

    private ConcurrentHashMap<String, RequestInfo> ipMap;
    private ScheduledExecutorService cleaner;

    @Override
    public void init(FilterConfig filterConfig) {
        String maxReq = filterConfig.getInitParameter("maxRequests");
        String win = filterConfig.getInitParameter("windowMillis");
        String clean = filterConfig.getInitParameter("cleanupIntervalSeconds");
        String img = filterConfig.getInitParameter("errorImage");
        try {
            if (maxReq != null) maxRequests = Integer.parseInt(maxReq);
            if (win != null) windowMillis = Long.parseLong(win);
            if (clean != null) cleanupIntervalSeconds = Long.parseLong(clean);
        } catch (NumberFormatException ignored) {}
        if (img != null) errorImageUrl = img.trim();

        ipMap = new ConcurrentHashMap<>();
        filterConfig.getServletContext().setAttribute(CONTEXT_ATTR, ipMap);

        cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "IpRateLimitCleaner");
            t.setDaemon(true);
            return t;
        });
        cleaner.scheduleWithFixedDelay(this::cleanupOldEntries,
                cleanupIntervalSeconds, cleanupIntervalSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() {
        if (cleaner != null) {
            cleaner.shutdownNow();
            cleaner = null;
        }
        if (ipMap != null) {
            ipMap.clear();
            ipMap = null;
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
            chain.doFilter(req, res);
            return;
        }

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        if (shouldSkipUri(uri)) {
            chain.doFilter(req, res);
            return;
        }

        String ip = extractClientIp(request);
        if (ip == null) ip = request.getRemoteAddr();

        long now = Instant.now().toEpochMilli();
        RequestInfo info = ipMap.computeIfAbsent(ip, k -> new RequestInfo(now));

        boolean allowed;
        int retryAfterSeconds = 0;

        synchronized (info) {
            if (now - info.windowStartMillis >= windowMillis) {
                info.windowStartMillis = now;
                info.count.set(1);
                info.lastSeen = now;
                allowed = true;
            } else {
                int cur = info.count.incrementAndGet();
                info.lastSeen = now;
                if (cur <= maxRequests) {
                    allowed = true;
                } else {
                    allowed = false;
                    retryAfterSeconds = Math.max(1, (int) ((info.windowStartMillis + windowMillis - now) / 1000));
                }
            }
        }

        if (!allowed) {
            request.setAttribute("maxRequests", maxRequests);
            request.setAttribute("windowMillis", windowMillis);
            request.setAttribute("retryAfterSeconds", retryAfterSeconds);
            request.setAttribute("errorImageUrl", resolveImageSrc(request));
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
            response.setStatus(429);
            response.setContentType("text/html; charset=UTF-8");
            RequestDispatcher rd = request.getRequestDispatcher("/tooManyRequests.jsp");
            rd.forward(request, response);
            return;
        }

        chain.doFilter(req, res);
    }

    private String resolveImageSrc(HttpServletRequest request) {
        String ctx = request.getContextPath() == null ? "" : request.getContextPath();
        if (errorImageUrl != null && !errorImageUrl.isBlank()) {
            String img = errorImageUrl.trim();
            if (img.startsWith("http://") || img.startsWith("https://")) {
                return img;
            } else if (img.startsWith("/")) {
                return ctx + img;
            } else {
                return ctx + "/" + img;
            }
        }
        return ctx + "/images/img.png";
    }

    private String extractClientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            String[] parts = xff.split(",");
            if (parts.length > 0) {
                String ip = parts[0].trim();
                if (!ip.isEmpty()) return ip;
            }
        }
        String xr = req.getHeader("X-Real-IP");
        if (xr != null && !xr.isBlank()) return xr.trim();
        return req.getRemoteAddr();
    }

    private boolean shouldSkipUri(String uri) {
        if (uri == null) return false;
        String u = uri.toLowerCase();
        if (u.endsWith(".css") || u.endsWith(".js") || u.endsWith(".png") ||
                u.endsWith(".jpg") || u.endsWith(".jpeg") ||
                u.endsWith(".svg") || u.endsWith(".map")) {
            return true;
        }
        return false;
    }

    private void cleanupOldEntries() {
        long now = Instant.now().toEpochMilli();
        long staleMillis = Math.max(windowMillis * 10, 30_000L);
        for (Map.Entry<String, RequestInfo> e : ipMap.entrySet()) {
            RequestInfo inf = e.getValue();
            if (now - inf.lastSeen > staleMillis) {
                ipMap.remove(e.getKey(), inf);
            }
        }
    }

    public static class RequestInfo {
        volatile long windowStartMillis;
        final AtomicInteger count = new AtomicInteger(0);
        volatile long lastSeen;

        RequestInfo(long now) {
            this.windowStartMillis = now;
            this.count.set(0);
            this.lastSeen = now;
        }
    }
}