package nl.prinsesmaximacentrum.sturgeon;

import java.awt.Taskbar;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
                    Main.setDockIcon();
                    Config config = Main.getConfig();
                    final SturgeonGUI wnd = new SturgeonGUI(Main.getColorConfig(), config, Main.getLogFile(config));
                    wnd.setVisible(true);
                } catch (Exception e) {
                    System.out.println("ERROR:");
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    private static void setDockIcon() {
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            try {
                taskbar.setIconImage(Main.getImageIcon());
            } catch (UnsupportedOperationException e) {
                System.err.println("The system does not support setting the icon image.");
            } catch (SecurityException e) {
                System.err.println("Security exception occurred while setting the icon image.");
            } catch (IOException e) {
                System.err.println("Failed to find dock icon");
            }
        }
    }

    private static Image getImageIcon() throws IOException, RuntimeException {
        InputStream imageStream = Main.class.getResourceAsStream("/icons/logo.png");
        if (imageStream == null) {
            throw new RuntimeException("Image not found: ");
        }
        return ImageIO.read(imageStream);
    }

    private static ColorConfig getColorConfig() throws IOException, StreamReadException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper.readValue(Main.class.getResourceAsStream("/ColorConfig.yml"), ColorConfig.class);
    }

    private static Config getConfig() throws IOException, StreamReadException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper.readValue(Main.class.getResourceAsStream("/Config.yml"), Config.class);
    }

    private static String getLogFile(Config config) throws IOException, NullPointerException {
        // Define the directory where logs will be stored, e.g., a temporary directory
        String logDirPath = config.getLogDir();
        File logDir = new File(logDirPath);

        // Create the directory if it doesn't exist
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        File file = new File(logDir, "/log_" +
               new SimpleDateFormat("yyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".txt");
        file.createNewFile();
        return file.getAbsolutePath();
    }
}