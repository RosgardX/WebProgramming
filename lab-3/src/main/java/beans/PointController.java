package beans;

import model.HitCalculator;
import model.PointResult;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.PrimeFaces;

@Named("pointController")
@ViewScoped
public class PointController implements Serializable {

    private static final long serialVersionUID = 1L;

    private double x;
    private double y;
    private double r = 1.0;

    private Double clickX;
    private Double clickY;
    private Double canvasWidth;
    private Double canvasHeight;

    @Inject
    private ResultBean resultsBean;

    @Inject
    private HitCalculator hitCalculator;

    public PointController() {
    }

    public String checkByInput() {
        if (!validateInputs()) {
            return null;
        }

        boolean hit = hitCalculator.isHit(x, y, r);
        PointResult pr = resultsBean.createAndStore(x, y, r, hit);

        PrimeFaces.current().ajax().addCallbackParam("last",
                Map.of("x", pr.getX(), "y", pr.getY(), "r", pr.getR(), "hit", pr.isHit()));

        addFacesMessage(FacesMessage.SEVERITY_INFO,
                String.format("Точка (%.2f, %.2f), R=%.2f — %s",
                        pr.getX(), pr.getY(), pr.getR(),
                        pr.isHit() ? "ПОПАДАНИЕ" : "МИМО"));
        return null;
    }
    public List<Map<String, Object>> getAllPoints() {
        List<Map<String, Object>> points = new ArrayList<>();
        if (resultsBean != null && resultsBean.getSessionResults() != null) {
            for (PointResult pr : resultsBean.getSessionResults()) {
                Map<String, Object> point = new HashMap<>();
                point.put("x", pr.getX());
                point.put("y", pr.getY());
                point.put("r", pr.getR());
                point.put("hit", pr.isHit());
                points.add(point);
            }
        }
        return points;
    }
    public String onCanvasClick() {
        if (clickX == null || clickY == null) {
            addFacesMessage(FacesMessage.SEVERITY_ERROR, "Координаты клика не переданы");
            return null;
        }
        if (canvasWidth == null || canvasHeight == null) {
            addFacesMessage(FacesMessage.SEVERITY_ERROR, "Размеры canvas не переданы");
            return null;
        }
        if (r <= 0) {
            addFacesMessage(FacesMessage.SEVERITY_ERROR, "Сначала выберите R");
            return null;
        }

        double width = canvasWidth;
        double height = canvasHeight;

        double cx = width / 2.0;
        double cy = height / 2.0;
        double scale = (width / 3.0) / r;

        double logicalX = (clickX - cx) / scale;
        double logicalY = (cy - clickY) / scale;

        this.x = logicalX;
        this.y = logicalY;

        boolean hit = hitCalculator.isHit(logicalX, logicalY, r);
        PointResult pr = resultsBean.createAndStore(logicalX, logicalY, r, hit);

        PrimeFaces.current().ajax().addCallbackParam("last",
                Map.of("x", pr.getX(), "y", pr.getY(), "r", pr.getR(), "hit", pr.isHit()));

        addFacesMessage(FacesMessage.SEVERITY_INFO,
                String.format("Клик: (%.2f, %.2f), R=%.2f — %s",
                        pr.getX(), pr.getY(), pr.getR(),
                        pr.isHit() ? "ПОПАДАНИЕ" : "МИМО"));

        return null;
    }

    public String changeRadius(double newR) {
        if (newR < 1.0 || newR > 5.0) {
            addFacesMessage(FacesMessage.SEVERITY_ERROR, "R должно быть 1..5");
            return null;
        }
        this.r = newR;
        addFacesMessage(FacesMessage.SEVERITY_INFO, "R установлен: " + r);
        return null;
    }

    private boolean validateInputs() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        if (Double.isNaN(x) || Double.isInfinite(x) || x < -3.0 || x > 3.0) {
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "X вне диапазона [-3; 3]", null));
            return false;
        }
        if (Double.isNaN(y) || Double.isInfinite(y) || y <= -5.0 || y >= 5.0) {
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Y вне диапазона (-5; 5)", null));
            return false;
        }
        if (Double.isNaN(r) || Double.isInfinite(r) || r < 1.0 || r > 5.0) {
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "R вне диапазона 1..5", null));
            return false;
        }
        return true;
    }

    private void addFacesMessage(FacesMessage.Severity severity, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, detail, null));
    }

    // getters / setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getR() { return r; }
    public void setR(double r) { this.r = r; }

    public Double getClickX() { return clickX; }
    public void setClickX(Double clickX) { this.clickX = clickX; }

    public Double getClickY() { return clickY; }
    public void setClickY(Double clickY) { this.clickY = clickY; }

    public Double getCanvasWidth() { return canvasWidth; }
    public void setCanvasWidth(Double canvasWidth) { this.canvasWidth = canvasWidth; }

    public Double getCanvasHeight() { return canvasHeight; }
    public void setCanvasHeight(Double canvasHeight) { this.canvasHeight = canvasHeight; }

    public ResultBean getResultsBean() { return resultsBean; }
    public void setResultsBean(ResultBean resultsBean) { this.resultsBean = resultsBean; }

    public HitCalculator getHitCalculator() { return hitCalculator; }
    public void setHitCalculator(HitCalculator hitCalculator) { this.hitCalculator = hitCalculator; }
}