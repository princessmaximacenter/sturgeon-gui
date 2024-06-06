package nl.prinsesmaximacentrum.sturgeon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Paths;

import static java.lang.Math.ceil;

public class Menu {

    private JButton setupButton, runningButton, predictionButton, cnvPlotButton, confidenceButton,
                    stopButton;
    private ImageIcon setupIcon, runningIcon, stopIcon;
    private ColorConfig colorConfig;

    public Menu(ColorConfig colorConfig) {
        this.colorConfig = colorConfig;
        this.setIcons();
        this.setButtons();
    }

    private void setIcons() {
        this.setupIcon = new ImageIcon(
                Menu.class.getResource("/icons/settings.png"),
                "Settings");
        this.runningIcon = new ImageIcon(
                Menu.class.getResource("/icons/running.png"),
                "Running");
        this.stopIcon = new ImageIcon(
                Menu.class.getResource("/icons/stop.png"),
                "Stop");
    }

    private void setIconSize(JButton button, ImageIcon imageIcon) {
        Rectangle size = button.getBounds();
        if (size.height != 0) {
            Image icon = imageIcon.getImage().getScaledInstance(
                    (int) ceil(size.height * 0.5),
                    (int) ceil(size.height * 0.5),
                    Image.SCALE_SMOOTH
            );
            imageIcon = new ImageIcon(icon);
            button.setIcon(imageIcon);
        }
    }

    private void setButtonFontSizes() {
        int size;
        for (JButton button : new JButton[] {setupButton, runningButton, stopButton}) {
            size = (int) ceil(((float) button.getBounds().height + button.getBounds().width) * 0.07);
            button.setFont(new Font("Arial", Font.BOLD, size));
        }
        for (JButton button : new JButton[] {confidenceButton, cnvPlotButton, predictionButton}) {
            size = (int) ceil(((float) button.getBounds().height + button.getBounds().width) * 0.07);
            button.setFont(new Font("Arial", Font.BOLD, (int) ceil(size * 0.8)));
        }
    }

    public void setSizes(){
        this.setIconSize(this.setupButton, this.setupIcon);
        this.setIconSize(this.runningButton, this.runningIcon);
        this.setIconSize(this.stopButton, this.stopIcon);
        this.setButtonFontSizes();
    }

    private void setButtons() {
        this.setupButton = new JButton("  Setup", this.setupIcon);
        this.setupButton.setBackground(colorConfig.getSetup());
        this.runningButton = new JButton("  Start", this.runningIcon);
        this.confidenceButton = new JButton("      - Confidence");
        this.predictionButton = new JButton("      - Prediction");
        this.cnvPlotButton = new JButton("      - CNV plot");
        this.stopButton = new JButton("  Stop", this.stopIcon);
        this.stopButton.setBackground(colorConfig.getStop());

        Color color = colorConfig.getRunning();
        for (JButton button : new JButton[]{runningButton, predictionButton, confidenceButton,
                cnvPlotButton}) {
            button.setBackground(color);
            color = color.darker();
        }

        for (JButton button : new JButton[]{setupButton, runningButton, confidenceButton, predictionButton,
                                            cnvPlotButton, stopButton}) {
            button.setOpaque(true);
            button.setForeground(Color.white);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFocusable(false);
            button.setHorizontalAlignment(SwingConstants.LEFT);

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            });
        }

        for (JButton button : new JButton[]{predictionButton, confidenceButton,
                cnvPlotButton, stopButton}) {
            button.setEnabled(false);
        }
    }

    public JButton getSetupButton() {
        return this.setupButton;
    }

    public JButton getRunningButton() {
        return runningButton;
    }

    public JButton getPredictionButton() {
        return predictionButton;
    }

    public JButton getCnvPlotButton() {
        return cnvPlotButton;
    }

    public JButton getConfidenceButton() {
        return confidenceButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }
}
