package com.transaction.service.entity;

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
@Table(name = "TRANSACTIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transactions {

    @Id
    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "CREDIT_ACCOUNT")
    private String creditAccountNumber;

    @Column(name = "DEBIT_ACCOUNT")
    private String debitAccountNumber;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DATE_CREATED")
    private Date dateCreated;
}
