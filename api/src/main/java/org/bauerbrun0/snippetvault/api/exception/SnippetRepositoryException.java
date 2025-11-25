package org.bauerbrun0.snippetvault.api.exception;

public class SnippetRepositoryException extends RuntimeException {
    public SnippetRepositoryException() {
        super();
    }

    public SnippetRepositoryException(String message) {
        super(message);
    }

    public SnippetRepositoryException(Throwable cause) {
        super(cause);
    }

    public SnippetRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
