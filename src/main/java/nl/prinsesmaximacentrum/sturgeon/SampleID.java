package nl.prinsesmaximacentrum.sturgeon;

import java.util.regex.Pattern;

/**
 * Class to store and validate sample identifiers
 * ToDo: Implement SampleID in the application
 */
public class SampleID {

    private String identifier;
    private final Pattern idPattern;

    public SampleID(String pattern) {
        this.idPattern = Pattern.compile(pattern);
    }

    /**
     * Check if the ID is matching the expected regex pattern
     * @param id: Identifier to check
     * @return if true, validation succeeded
     */
    public boolean isIdValid(String id) {
        return idPattern.matcher(id).find();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = (this.isIdValid(identifier)) ? identifier : "";
    }
}
