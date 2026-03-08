package com.gateway.microservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetAccountResponseDTO {

    private List<Account> accounts;
    private Integer page;
    private Integer maxRow;

    public class Account {
        private Integer accountNumber;
        private String customerName;
        private Integer balance;
    }
}
