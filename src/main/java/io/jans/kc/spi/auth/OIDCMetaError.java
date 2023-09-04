package io.jans.kc.spi.auth;

public class OIDCMetaError extends Exception {
    
    public OIDCMetaError(String message) {
        super(message);
    }

    public OIDCMetaError(String message, Throwable cause) {
        super(message,cause);
    }
}
