package DB;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import model.PointResult;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named("databaseService")
@ApplicationScoped
public class DataBaseService {

    private static final String JNDI_NAME = "java:jboss/datasources/PointsDS";

    private DataSource dataSource;

    public DataBaseService() {
    }

    @PostConstruct
    public void init() {
        try {
            InitialContext ctx = new InitialContext();
            this.dataSource = (DataSource) ctx.lookup(JNDI_NAME);
        } catch (NamingException e) {
            throw new IllegalStateException(
                    "Не удалось найти DataSource по JNDI '" + JNDI_NAME + "'. " +
                            "Убедись, что datasource настроен в WildFly.", e);
        }
    }

    private Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource не инициализирован. Проверь init() и конфигурацию сервера.");
        }
        return dataSource.getConnection();
    }

    public void saveResult(PointResult result) {
        final String sql = "INSERT INTO results (x, y, r, hit, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, result.getX());
            ps.setDouble(2, result.getY());
            ps.setDouble(3, result.getR());
            ps.setBoolean(4, result.isHit());
            LocalDateTime created = result.getCreatedAt() != null
                    ? result.getCreatedAt()
                    : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(created));

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении результата в БД", e);
        }
    }

    public List<PointResult> fetchLast(int limit) {
        final String sql = "SELECT x, y, r, hit, created_at " +
                "FROM results ORDER BY created_at DESC LIMIT ?";
        List<PointResult> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double r = rs.getDouble("r");
                    boolean hit = rs.getBoolean("hit");
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime created = (ts != null) ? ts.toLocalDateTime() : LocalDateTime.now();

                    PointResult pr = new PointResult(x, y, r, hit, created);
                    list.add(pr);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при чтении результатов из БД", e);
        }
        return list;
    }

    public boolean testConnection() {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT 1")) {
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}