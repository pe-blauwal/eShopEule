package org.senju.eshopeule.exceptions;

public class ObjectAlreadyExistsException extends RuntimeException {
    public ObjectAlreadyExistsException() {super();}
    public ObjectAlreadyExistsException(String message) {super(message);}
    public ObjectAlreadyExistsException(String message, Throwable cause) {super(message, cause);}
    public ObjectAlreadyExistsException(Throwable cause) {super(cause);}
}
