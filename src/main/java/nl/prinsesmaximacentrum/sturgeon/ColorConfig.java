package nl.prinsesmaximacentrum.sturgeon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.awt.*;

public class ColorConfig {

    @JsonDeserialize(using = ColorDeserializer.class)
    private Color background;

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }
}
