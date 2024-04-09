package nl.prinsesmaximacentrum.sturgeon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SturgeonGUI extends JFrame {

    private Container window;
    private ColorConfig colorConfig;

    public SturgeonGUI(ColorConfig colorConfig) {
        super("Sturgeon");
        this.colorConfig = colorConfig;
        this.setClosure();
        this.setWindow();
    }

    private void setClosure() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SturgeonGUI.this.setVisible(false);
                SturgeonGUI.this.dispose();
            }
        });
    }

    private void setWindow() {
        this.window = getContentPane();
        this.window.setLayout(new GridBagLayout());
        this.window.setBackground(this.colorConfig.getBackground());
    }

    private void buildGUI() {

    }

}
