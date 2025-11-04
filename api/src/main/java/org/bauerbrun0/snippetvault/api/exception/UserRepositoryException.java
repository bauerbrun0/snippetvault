package org.bauerbrun0.snippetvault.api.exception;

public class UserRepositoryException extends RuntimeException {
    public UserRepositoryException() {
        super();
    }

    public UserRepositoryException(String message) {
        super(message);
    }

    public UserRepositoryException(Throwable cause) {
        super(cause);
    }

    public UserRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
