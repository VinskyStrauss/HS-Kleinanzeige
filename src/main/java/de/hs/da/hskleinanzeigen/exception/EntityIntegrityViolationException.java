package de.hs.da.hskleinanzeigen.exception;

public class EntityIntegrityViolationException extends RuntimeException{
    public EntityIntegrityViolationException(String type, String id) {
        super("Entity with type: " + type + " and id: " + id + " already exists");
    }
}
