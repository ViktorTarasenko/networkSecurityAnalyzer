package com.ai.neural;

/**
 * Created by victor on 05.12.15.
 */
public class NeuralException extends Exception {
    public NeuralException() {
    }

    public NeuralException(String message) {
        super(message);
    }

    public NeuralException(String message, Throwable cause) {
        super(message, cause);
    }

    public NeuralException(Throwable cause) {
        super(cause);
    }

    public NeuralException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
