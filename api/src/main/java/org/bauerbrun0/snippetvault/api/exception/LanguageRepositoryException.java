package org.bauerbrun0.snippetvault.api.exception;

public class LanguageRepositoryException extends RuntimeException {
    public LanguageRepositoryException(String message) {
        super(message);
    }

    public LanguageRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public LanguageRepositoryException(Throwable cause) {
        super(cause);
    }

    public LanguageRepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LanguageRepositoryException() {
    }
}
