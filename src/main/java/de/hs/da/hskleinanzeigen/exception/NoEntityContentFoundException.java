package de.hs.da.hskleinanzeigen.exception;

public class NoEntityContentFoundException extends RuntimeException{
    public NoEntityContentFoundException(String type) {
        super("No Entity with type: " + type + " found");
    }
}
