package com.group6b.shopiifoodwebsite.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum orderStatus {
    PENDING(1),
    CONFIRMED(2),
    IN_PROGRESS(3),
    COMPLETED(4),
    CANCELLED(5);
    public final long value;
}
