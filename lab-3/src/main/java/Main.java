import java.io.*;
import java.net.Socket;
import java.nio.file.*;

public class Main {

    private static final String WILDFLY_HOME = "D:/WildFly/wildfly-37.0.1.Final/wildfly-37.0.1.Final";
    private static final String WAR_NAME = "opi3.war";
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 5433;
    private static final int DB_WAIT_SECONDS = 60;

    public static void main(String[] args) {
        System.out.println("=== OPI3 Лаунчер ===");

        String wildflyHome = System.getenv("WILDFLY_HOME");
        if (wildflyHome == null || wildflyHome.isBlank()) {
            wildflyHome = WILDFLY_HOME;
            System.out.println("WILDFLY_HOME не задан, используется путь по умолчанию: " + wildflyHome);
        } else {
            System.out.println("WILDFLY_HOME = " + wildflyHome);
        }

        startDatabase();
        waitForDatabase();
        deployWar(wildflyHome);
        startWildFly(wildflyHome);
    }

    private static void startDatabase() {
        System.out.println("Запуск базы данных через docker-compose...");
        try {
            boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
            ProcessBuilder pb;
            if (isWindows) {
                pb = new ProcessBuilder("cmd.exe", "/c", "docker-compose", "up", "-d", "db");
            } else {
                pb = new ProcessBuilder("docker-compose", "up", "-d", "db");
            }
            pb.inheritIO();
            Process p = pb.start();
            int code = p.waitFor();
            if (code != 0) {
                System.err.println("Предупреждение: docker-compose завершился с кодом " + code);
                System.err.println("Убедитесь что Docker Desktop запущен.");
            } else {
                System.out.println("Docker-compose выполнен успешно.");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Не удалось запустить docker-compose: " + e.getMessage());
            System.err.println("Убедитесь что Docker Desktop запущен и docker-compose доступен в PATH.");
        }
    }

    private static void waitForDatabase() {
        System.out.println("Ожидание готовности PostgreSQL на " + DB_HOST + ":" + DB_PORT + "...");
        long deadline = System.currentTimeMillis() + DB_WAIT_SECONDS * 1000L;
        while (System.currentTimeMillis() < deadline) {
            try (Socket s = new Socket(DB_HOST, DB_PORT)) {
                System.out.println("PostgreSQL готов.");
                return;
            } catch (IOException e) {
                System.out.print(".");
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }
        System.out.println();
        System.err.println("Предупреждение: PostgreSQL не ответил за " + DB_WAIT_SECONDS + " секунд, продолжаем...");
    }

    private static void deployWar(String wildflyHome) {
        File deployDir = new File(wildflyHome, "standalone/deployments");
        if (!deployDir.exists()) {
            System.err.println("Ошибка: папка deployments не найдена: " + deployDir.getAbsolutePath());
            System.err.println("Проверьте переменную окружения WILDFLY_HOME.");
            System.exit(2);
        }

        File warDest = new File(deployDir, WAR_NAME);
        System.out.println("Извлечение " + WAR_NAME + " из JAR-архива...");

        try (InputStream is = Main.class.getResourceAsStream("/resources/" + WAR_NAME)) {
            if (is == null) {
                System.err.println("Ошибка: " + WAR_NAME + " не найден внутри JAR.");
                System.err.println("Убедитесь что сборка выполнена командой: ant build");
                System.exit(3);
            }
            Files.copy(is, warDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("WAR развёрнут: " + warDest.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Ошибка при извлечении WAR: " + e.getMessage());
            System.exit(3);
        }
    }

    private static void startWildFly(String wildflyHome) {
        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        File binDir = new File(wildflyHome, "bin");

        System.out.println("Запуск WildFly...");
        try {
            ProcessBuilder pb;
            if (isWindows) {
                pb = new ProcessBuilder("cmd.exe", "/c", "start", "\"WildFly\"", "standalone.bat");
            } else {
                pb = new ProcessBuilder("bash", "standalone.sh", "-b", "0.0.0.0");
            }
            pb.directory(binDir);
            pb.start();

            System.out.println("WildFly успешно запущен.");
            System.out.println("Приложение будет доступно по адресу: http://localhost:8080/" + WAR_NAME.replace(".war", ""));
            System.out.println("Работа лаунчера завершена. Процесс JAR завершается.");
            System.exit(0);
        } catch (IOException e) {
            System.err.println("Ошибка при запуске WildFly: " + e.getMessage());
            System.exit(4);
        }
    }
}