package com.gateway.microservice.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
public class GetAccountDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Integer nik;
        private Integer accountNumber;
        private String customerName;
        private Integer balance;
        private Date dateCreated;
        private Integer page;
        private Integer maxRow;
    }
}
