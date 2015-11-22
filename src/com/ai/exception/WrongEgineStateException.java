package com.ai.exception;

/**
 *
 */
public class WrongEgineStateException extends PacketCaptureException{
    public WrongEgineStateException() {
    }

    public WrongEgineStateException(String message) {
        super(message);
    }

    public WrongEgineStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongEgineStateException(Throwable cause) {
        super(cause);
    }

    public WrongEgineStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
