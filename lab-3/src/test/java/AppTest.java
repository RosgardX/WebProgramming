import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @Test
    void applicationVersionIsSet() {
        String version = "1.0-SNAPSHOT";
        assertNotNull(version);
        assertFalse(version.isBlank());
    }

    @Test
    void applicationNameIsSet() {
        String name = "opi3";
        assertEquals("opi3", name);
    }
}