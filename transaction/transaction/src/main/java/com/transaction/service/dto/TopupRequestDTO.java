package com.transaction.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopupRequestDTO {
    private String accountNumber;
    private BigDecimal amount;
    private String transactionId;
}
