package com.mchern1kov.model;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Transfer {
    private Long id;
    private Long fromId;
    private Long toId;
    private BigDecimal amount;
    // Balance before/after, timestamps etc.
}
