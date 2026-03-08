package com.transaction.service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMetricsDTO {
    private long totalSuccessfulTransactions;
    private BigDecimal totalTransactionVolume;
    private boolean error;
    private String message;
}
