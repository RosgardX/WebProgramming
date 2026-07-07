package beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Named("clockBean")
@ApplicationScoped
public class ClockBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final ZoneId zoneId = ZoneId.systemDefault();

    public String getCurrentTime() {
        return LocalDateTime.now(zoneId).format(formatter);
    }
}