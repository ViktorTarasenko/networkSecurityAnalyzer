package com.ai.exception;

/**
 *
 */
public class PacketCaptureException extends Exception {
    public PacketCaptureException() {
    }

    public PacketCaptureException(String message) {
        super(message);
    }

    public PacketCaptureException(String message, Throwable cause) {
        super(message, cause);
    }

    public PacketCaptureException(Throwable cause) {
        super(cause);
    }

    public PacketCaptureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
