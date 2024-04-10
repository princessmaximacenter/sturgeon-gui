package nl.prinsesmaximacentrum.sturgeon;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;

import static java.lang.Math.ceil;

public class Menu {

    private JLabel setupLabel, runningLabel;
    private ImageIcon setupIcon, runningIcon;
    private JLabel active;

    public Menu() {
        this.setIcons();
        this.setLabels();
        this.setActive(this.setupLabel);
    }

    private void setIcons() {
        String relativePath = "src/main/resources/icons/";
        this.setupIcon = new ImageIcon(
                Paths.get(relativePath + "settings.png").toAbsolutePath().toString(),
                "Settings");
        this.runningIcon = new ImageIcon(
                Paths.get( relativePath+ "running.png").toAbsolutePath().toString(),
                "Running");
    }

    private void setIconSize(JLabel label, ImageIcon imageIcon) {
        Rectangle size = label.getBounds();
        if (size.height != 0) {
            Image icon = imageIcon.getImage().getScaledInstance(
                    (int) ceil(size.height * 0.5),
                    (int) ceil(size.height * 0.5),
                    Image.SCALE_SMOOTH
            );
            imageIcon = new ImageIcon(icon);
            label.setIcon(imageIcon);
        }
    }

    private void setLabelFontSizes() {
        int size;
        for (JLabel label : new JLabel[] {this.setupLabel, this.runningLabel}) {
            label.setForeground(Color.LIGHT_GRAY);
            size = (int) ceil(((float) label.getBounds().height + label.getBounds().width) * 0.07);
            label.setFont(new Font("Arial",
                    (this.active == label) ? Font.BOLD : Font.PLAIN,
                    size));
            if (this.active == label) {
                label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3));
            } else {
                label.setBorder(BorderFactory.createEmptyBorder());
            }
        }
    }

    public void setSizes(){
        this.setIconSize(this.setupLabel, this.setupIcon);
        this.setIconSize(this.runningLabel, this.runningIcon);
        this.setLabelFontSizes();
    }

    private void setLabels() {
        this.setupLabel = new JLabel("  Setup", this.setupIcon, JLabel.LEFT);
        this.runningLabel = new JLabel("  Running", this.runningIcon, JLabel.LEFT);
    }

    public JLabel getSetupLabel() {
        return setupLabel;
    }

    public JLabel getRunningLabel() {
        return runningLabel;
    }

    public void setActive(JLabel label) {
        this.active = label;
    }
}
