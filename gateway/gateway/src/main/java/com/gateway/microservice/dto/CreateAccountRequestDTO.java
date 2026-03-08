package com.gateway.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequestDTO {
    private String nik;
    private String customerName;
    private BigDecimal balance;
}
