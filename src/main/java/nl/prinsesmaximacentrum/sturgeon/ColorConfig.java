package nl.prinsesmaximacentrum.sturgeon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.awt.*;

public class ColorConfig {

    @JsonDeserialize(using = ColorDeserializer.class)
    private Color menu, display, terminal;

    public Color getMenu() {
        return menu;
    }

    public void setMenu(Color menu) {
        this.menu = menu;
    }

    public Color getDisplay() {
        return display;
    }

    public void setDisplay(Color display) {
        this.display = display;
    }

    public Color getTerminal() {
        return terminal;
    }

    public void setTerminal(Color terminal) {
        this.terminal = terminal;
    }
}
