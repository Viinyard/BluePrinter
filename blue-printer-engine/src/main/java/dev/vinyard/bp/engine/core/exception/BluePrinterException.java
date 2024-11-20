package dev.vinyard.bp.engine.core.exception;

public class BluePrinterException extends Exception {
    public BluePrinterException(String message) {
        super(message);
    }

    public BluePrinterException(String message, Throwable cause) {
        super(message, cause);
    }
}
