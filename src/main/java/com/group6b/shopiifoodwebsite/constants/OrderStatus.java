package com.group6b.shopiifoodwebsite.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    PENDING(1),
    CONFIRMED(2),
    IN_PROGRESS(3),
    COMPLETED(4),
    CANCELLED(5);
    public final long value;

    public static OrderStatus fromValue(long value) {
        for (OrderStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid OrderStatus value: " + value);
    }
}
