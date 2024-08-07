package com.group6b.shopiifoodwebsite.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Role {
    ADMIN(1),
    USER(2),
    SELLER(3);
    public final long value;
}
