package com.transaction.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private String transactionId;
    private String creditAccountNumber;
    private String debitAccountNumber;
    private BigDecimal amount;
    private String currency;
    private String status;
    private Date dateCreated;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestTransaction {
        private String creditAccountNumber;
        private String debitAccountNumber;
        private BigDecimal amount;
        private String currency;
    }
}
