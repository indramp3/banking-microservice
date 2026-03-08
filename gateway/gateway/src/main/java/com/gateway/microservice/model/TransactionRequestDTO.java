package com.gateway.microservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionRequestDTO {

    private Date startDate;
    private Date endDate;
    private String accountNumber;
    private String transactionId;
    private Integer page;
    private Integer maxRow;

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
