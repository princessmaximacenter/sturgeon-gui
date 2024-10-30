package nl.prinsesmaximacentrum.sturgeon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.awt.*;

/**
 * Class containing all the colors used throughout the application
 */
public class ColorConfig {

    @JsonDeserialize(using = ColorDeserializer.class)
    private Color menu, setup, running, terminal, fileButton, stop;

    public Color getMenu() {
        return menu;
    }

    public void setMenu(Color menu) {
        this.menu = menu;
    }

    public Color getSetup() {
        return setup;
    }

    public void setSetup(Color setup) {
        this.setup = setup;
    }

    public Color getTerminal() {
        return terminal;
    }

    public void setTerminal(Color terminal) {
        this.terminal = terminal;
    }

    public Color getFileButton() {
        return fileButton;
    }

    public void setFileButton(Color fileButton) {
        this.fileButton = fileButton;
    }

    public Color getRunning() {
        return running;
    }

    public void setRunning(Color running) {
        this.running = running;
    }

    public Color getStop() {
        return stop;
    }

    public void setStop(Color stop) {
        this.stop = stop;
    }
}
