package com.transaction.service.service.impl;

import com.transaction.service.dto.TransactionDTO;
import com.transaction.service.repository.TransactionRepository;
import com.transaction.service.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void createTransaction(TransactionDTO.RequestTransaction requestTransaction) {

        try{
            log.info("Creating Transaction");

            //todo: valiadation debit account number ke table accounts

            //todo: valiadation credit account number ke table accounts

            //todo: validation balance ke table accounts

        }catch (Exception e){
            log.error("Error while creating transaction", e);
        }

    }
}
