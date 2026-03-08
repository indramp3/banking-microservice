package com.transaction.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "TRANSACTION_HISTORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistory {

    @Id
    @Column(name = "HISTORY_ID")
    private String historyId;

    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DATE_CREATED")
    private Date dateCreated;
}
