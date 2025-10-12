package com.warehouse.common.exceptions;

public class CapacityReductionNotAllowedException extends RuntimeException {
    public CapacityReductionNotAllowedException(String message) {
        super(message);
    }
}
