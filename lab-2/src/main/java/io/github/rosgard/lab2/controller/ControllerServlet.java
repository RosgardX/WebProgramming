package io.github.rosgard.lab2.controller;

import io.github.rosgard.lab2.service.HitResultRepository;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/app/*")
public class ControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getPathInfo();
        if (path != null) {
            path = path.trim();
        }

        if ("/clear".equals(path)) {
            clearHistory(req);
            resp.sendRedirect(req.getContextPath() + "/app/");
            return;
        }

        if (hasAllPointParams(req)) {
            req.getRequestDispatcher("/area-check").forward(req, resp);
        } else {
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        }
    }


    private boolean hasAllPointParams(HttpServletRequest req) {
        return req.getParameter("x") != null &&
                req.getParameter("y") != null &&
                req.getParameter("r") != null;
    }

    private void clearHistory(HttpServletRequest req) {
        ServletContext ctx = req.getServletContext();
        Object repoObj = ctx.getAttribute("hitResultRepository");
        if (repoObj instanceof HitResultRepository repo) {
            repo.clear();
        }
    }
}