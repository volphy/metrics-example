package com.sample.metrics;

/**
 * Domain exception thrown if metrics generation went wrong.
 * @author Krzysztof Wilk
 */
public class MetricsException extends Exception {

    /**
     * Public constructor
     * @param message human-readable error message
     */
    public MetricsException(String message) {
        super(message);
    }
}
