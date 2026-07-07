package beans;

import DB.DataBaseService;
import model.PointResult;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Named("resultsBean")
@SessionScoped
public class ResultBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<PointResult> sessionResults = new ArrayList<>();

    @Inject
    private DataBaseService databaseService;

    private int initialLoadLimit = 10;

    public ResultBean() {
    }

    @PostConstruct
    public void init() {
    }

    public PointResult createAndStore(double x, double y, double r, boolean hit) {
        PointResult pr = new PointResult(x, y, r, hit, LocalDateTime.now());

        sessionResults.add(0, pr);

        try {
            databaseService.saveResult(pr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pr;
    }

    public List<PointResult> getSessionResults() {
        return Collections.unmodifiableList(sessionResults);
    }

    public DataBaseService getDatabaseService() {
        return databaseService;
    }

    public void setDatabaseService(DataBaseService databaseService) {
        this.databaseService = databaseService;
    }

    public int getInitialLoadLimit() {
        return initialLoadLimit;
    }

    public void setInitialLoadLimit(int initialLoadLimit) {
        this.initialLoadLimit = initialLoadLimit;
    }
}