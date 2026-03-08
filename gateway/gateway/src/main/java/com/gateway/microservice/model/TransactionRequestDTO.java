package com.gateway.microservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

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
        private Integer creditAccountNumber;
        private Integer debitAccountNumber;
        private Integer amount;
        private String currency;
    }
}
