package nl.prinsesmaximacentrum.sturgeon;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {

    private String logPath;

    public Logger(String logPath) {
        this.logPath = logPath;
    }

    public void addToLog(String msg) {
        try (FileWriter fileWriter = new FileWriter(this.logPath, true)) {
            fileWriter.write(
                    new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss|").format(Calendar.getInstance().getTime()) +
                            System.getProperty("user.name") + "| " + msg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR: " + e.getMessage());
        }
    }
}
