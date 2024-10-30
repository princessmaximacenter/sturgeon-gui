package nl.prinsesmaximacentrum.sturgeon;

import java.awt.Color;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;

/**
 * Class to turn a string into a Color object
 */
public class ColorDeserializer extends StdDeserializer<Color> {

    public ColorDeserializer() {
        this(null);
    }

    public ColorDeserializer(Class<?> vc) {
        super(vc);
    }

    /**
     * Method to convert a csv string into a color object
     * @param jp: Input json containing the csv string
     * @param ctxt: Object given by the JsonDeserialize tag
     * @return Color object
     * @throws IOException In case something is wrong with the csv string
     */
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
