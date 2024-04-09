package nl.prinsesmaximacentrum.sturgeon;

import java.awt.Color;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;

public class ColorDeserializer extends StdDeserializer<Color> {

    public ColorDeserializer() {
        this(null);
    }

    public ColorDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Color deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        String value = jp.getValueAsString();
        // Assuming color value in format "r,g,b"
        String[] rgb = value.split(",");
        int r = Integer.parseInt(rgb[0]);
        int g = Integer.parseInt(rgb[1]);
        int b = Integer.parseInt(rgb[2]);
        return new Color(r, g, b);
    }
}
