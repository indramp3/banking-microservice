package com.acount.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {
    private String debitAccountNumber;
    private String creditAccountNumber;
    private BigDecimal amount;
    private String transactionId;
}
