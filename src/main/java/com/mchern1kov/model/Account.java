package com.mchern1kov.model;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Account {
    private Long id;
    private String fullName;
    private BigDecimal balance;
}
