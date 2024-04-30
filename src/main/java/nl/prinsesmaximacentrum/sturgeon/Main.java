package nl.prinsesmaximacentrum.sturgeon;

import com.apple.eawt.Application;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName());
                    Application application = Application.getApplication();
                    application.setDockIconImage(Toolkit.getDefaultToolkit().getImage(
                            Paths.get("src/main/resources/icons/logo.png").toAbsolutePath().toString()));
                    final SturgeonGUI wnd = new SturgeonGUI(Main.getColorConfig(), Main.getConfig(), Main.getLogFile());
                    wnd.setVisible(true);
                } catch (Exception e) {
                    System.out.println("ERROR:");
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    private static ColorConfig getColorConfig() throws DatabindException, IOException, StreamReadException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper.readValue(new File("src/main/resources/ColorConfig.yml"), ColorConfig.class);
    }

    private static Config getConfig() throws DatabindException, IOException, StreamReadException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper.readValue(new File("src/main/resources/Config.yml"), Config.class);
    }

    private static String getLogFile() throws IOException {
       File file = new File("src/main/resources/logs/log_" +
               new SimpleDateFormat("yyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".txt");
       file.createNewFile();
       return file.getAbsolutePath();
    }
}