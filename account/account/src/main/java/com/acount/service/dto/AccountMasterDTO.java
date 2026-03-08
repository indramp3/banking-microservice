package com.acount.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class AccountMasterDTO {

    private String nik;
    private String accountNumber;
    private String customerName;
    private BigDecimal balance;
    private String dateCreated;

    private Integer page;
    private Integer maxRow;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class RequestCreateAccount{
        private String nik;
        private String customerName;
        private BigDecimal balance;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResponseCreateAccount{
        private Boolean error;
        private String message;
        private DataResp result;
        private List<DataResp> listResult;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DataResp implements Serializable {
        private Integer id;
        private String name;
        private Integer price;
    }
}
