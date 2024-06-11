package com.group6b.shopiifoodwebsite.daos;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    private Long foodId;
    private String foodName;
    private Double price;
    private int quantity;
}
