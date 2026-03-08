package com.transaction.service.service;

import com.transaction.service.dto.TransactionDTO;

import java.util.Date;
import java.util.List;

public interface TransactionService {

    void createTransaction(TransactionDTO.RequestTransaction requestTransaction);

    TransactionDTO getTransactionDetail(String transactionId);

    List<TransactionDTO> getTransactionsByAccount(String accountNumber, int page, int maxRow);

    List<TransactionDTO> getTransactionHistory(Date startDate, Date endDate, int page, int maxRow);
}
