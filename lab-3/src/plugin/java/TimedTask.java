import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public class TimedTask extends Task {

    private String target;
    private String label = "Выполнение таргета";

    public void setTarget(String target) {
        this.target = target;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void execute() throws BuildException {
        if (target == null || target.isBlank()) {
            throw new BuildException("Атрибут 'target' обязателен для таска <timed>");
        }

        if (getProject().getTargets().get(target) == null) {
            throw new BuildException("Таргет '" + target + "' не найден в build.xml");
        }

        printSeparator();
        log(label + " [таргет: " + target + "]", Project.MSG_INFO);
        printSeparator();

        long start = System.currentTimeMillis();

        try {
            getProject().executeTarget(target);
        } catch (BuildException e) {
            long elapsed = System.currentTimeMillis() - start;
            printSeparator();
            log(" ПРОВАЛ: " + label, Project.MSG_ERR);
            log(" Время до ошибки: " + formatTime(elapsed), Project.MSG_ERR);
            printSeparator();
            throw e;
        }

        long elapsed = System.currentTimeMillis() - start;

        printSeparator();
        log(" УСПЕХ: " + label, Project.MSG_INFO);
        log(" Затрачено времени: " + formatTime(elapsed), Project.MSG_INFO);
        printSeparator();
    }

    private String formatTime(long millis) {
        if (millis < 1000) {
            return millis + " мс";
        } else if (millis < 60000) {
            return String.format("%.2f сек", millis / 1000.0);
        } else {
            long minutes = millis / 60000;
            long seconds = (millis % 60000) / 1000;
            return minutes + " мин " + seconds + " сек";
        }
    }

    private void printSeparator() {
        log("-----------------------------", Project.MSG_INFO);
    }
}