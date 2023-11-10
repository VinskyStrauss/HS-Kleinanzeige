package de.hs.da.hskleinanzeigen.exception;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String type, String id) {
        super("Entity with type: " + type + ", with id: " + id + " NOT found");
    }

    public EntityNotFoundException(String type, int id) {
        super("Entity with type: " + type + ", with id: " + id + " NOT found");
    }
}
