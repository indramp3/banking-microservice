package com.acount.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "ACCOUNT_MASTER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountMaster {

    @Id
    @Column(name = "NIK")
    private String nik;

    @Column(name = "ACCOUNT_NUMBER")
    private String accountNumber;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    @Column(name = "BALANCE")
    private BigDecimal balance;

    @Column(name = "DATE_CREATED")
    private Date dateCreated;
}
