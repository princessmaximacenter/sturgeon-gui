package nl.prinsesmaximacentrum.sturgeon;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final SturgeonGUI wnd = new SturgeonGUI(Main.getColorConfig());
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
}