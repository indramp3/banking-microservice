package com.transaction.service.service;

import com.transaction.service.dto.TransactionDTO;

public interface TransactionService {

    void createTransaction(TransactionDTO.RequestTransaction requestTransaction);
}
