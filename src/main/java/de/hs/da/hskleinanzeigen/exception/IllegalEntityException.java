package de.hs.da.hskleinanzeigen.exception;

public class IllegalEntityException extends RuntimeException {
    public IllegalEntityException(String type, String name) {
        super("Illegal Entity, type: " + type + ", with name: " + name);
    }

    public IllegalEntityException(String type, String name, String message) {
        super("Illegal Entity, type: " + type + ", with name: " + name + ", message: " + message);
    }
}
