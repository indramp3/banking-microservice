package com.acount.service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountMetricsDTO {
    private long totalAccounts;
    private BigDecimal totalBalance;
    private boolean error;
    private String message;
}
