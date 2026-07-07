package io.github.rosgard.lab2.controller;

import io.github.rosgard.lab2.service.HitResultRepository;
import io.github.rosgard.lab2.service.HitService;
import io.github.rosgard.lab2.service.ValidationService;
import io.github.rosgard.lab2.service.HitService.HitResult;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/area-check")
public class AreaCheckServlet extends HttpServlet {

    private HitService hitService;
    private ValidationService validation;
    private HitResultRepository repo;

    @Override
    public void init() {
        hitService = new HitService();
        validation = new ValidationService();
        ServletContext sc = getServletContext();
        repo = (HitResultRepository) sc.getAttribute("hitResultRepository");
        if (repo == null) {
            repo = new HitResultRepository();
            sc.setAttribute("hitResultRepository", repo);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String xs = need(req, "x");
            String ys = need(req, "y");
            String rs = need(req, "r");

            double x = Double.parseDouble(xs.replace(',', '.'));
            double y = Double.parseDouble(ys.replace(',', '.'));
            double r = Double.parseDouble(rs.replace(',', '.'));

            validation.validate(x, y, r);

            long ctMillis = parseLong(req.getParameter("clientTimeMillis"), -1L);
            String ctText = req.getParameter("clientTimeText");

            HitResult hr = hitService.compute(x, y, r, ctMillis, ctText);
            repo.add(hr);
            req.setAttribute("currentResult", hr);
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
        } catch (NumberFormatException ex) {
            req.setAttribute("error", "Неверный числовой формат параметров");
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
        } catch (IllegalArgumentException ex) {
            req.setAttribute("error", ex.getMessage());
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
        }
    }

    private static String need(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Отсутствует параметр: " + name);
        return v.trim();
    }

    private static long parseLong(String s, long def) {
        try { return (s == null) ? def : Long.parseLong(s.trim()); }
        catch (Exception e) { return def; }
    }
}