package com.transaction.service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.service.dto.AccountResponseDTO;
import com.transaction.service.dto.TransactionDTO;
import com.transaction.service.dto.TransferRequestDTO;
import com.transaction.service.entity.TransactionHistory;
import com.transaction.service.entity.Transactions;
import com.transaction.service.repository.TransactionHistoryRepository;
import com.transaction.service.repository.TransactionRepository;
import com.transaction.service.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.update-balance-req}")
    private String updateBalanceTopicReq;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            TransactionHistoryRepository transactionHistoryRepository,
            ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate,
            ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.replyingKafkaTemplate = replyingKafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void createTransaction(TransactionDTO.RequestTransaction request) {
        String transactionId = UUID.randomUUID().toString();
        log.info("Creating Transaction ID: {}", transactionId);

        Transactions transaction = new Transactions();
        transaction.setTransactionId(transactionId);
        transaction.setCreditAccountNumber(request.getCreditAccountNumber());
        transaction.setDebitAccountNumber(request.getDebitAccountNumber());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setStatus("PENDING");
        transaction.setDateCreated(new Date());

        transactionRepository.saveAndFlush(transaction);
        saveTransactionHistory(transactionId, "PENDING");

        try {
            TransferRequestDTO transferReq = new TransferRequestDTO(
                    request.getDebitAccountNumber(),
                    request.getCreditAccountNumber(),
                    request.getAmount(),
                    transactionId
            );

            log.info("Sending update-balance-req to Account Service for Tx: {}", transactionId);
            ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(updateBalanceTopicReq, transferReq);
            RequestReplyFuture<String, Object, Object> replyFuture = replyingKafkaTemplate.sendAndReceive(producerRecord);
            ConsumerRecord<String, Object> consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);

            AccountResponseDTO response = objectMapper.convertValue(consumerRecord.value(), AccountResponseDTO.class);

            if (response.getError() != null && !response.getError()) {
                transaction.setStatus("SUCCESS");
                saveTransactionHistory(transactionId, "SUCCESS");
                log.info("Transaction {} Success!", transactionId);
            } else {
                transaction.setStatus("FAILED");
                saveTransactionHistory(transactionId, "FAILED");
                log.error("Transaction {} Failed at Account Service: {}", transactionId, response.getMessage());
            }
        } catch (Exception e) {
            transaction.setStatus("FAILED");
            saveTransactionHistory(transactionId, "FAILED");
            log.error("Error communicating with Account Service for Tx: {}", transactionId, e);
        }

        transactionRepository.save(transaction);
    }

    private void saveTransactionHistory(String transactionId, String status) {
        TransactionHistory history = new TransactionHistory(
                UUID.randomUUID().toString(),
                transactionId,
                status,
                new Date()
        );
        transactionHistoryRepository.save(history);
    }

    @Override
    public TransactionDTO getTransactionDetail(String transactionId) {
        Transactions tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        return mapToDTO(tx);
    }

    @Override
    public List<TransactionDTO> getTransactionsByAccount(String accountNumber, int page, int maxRow) {
        Pageable pageable = PageRequest.of(page, maxRow, Sort.by("dateCreated").descending());
        Page<Transactions> transactionsPage = transactionRepository.findByAccountNumber(accountNumber, pageable);
        return transactionsPage.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionHistory(Date startDate, Date endDate, int page, int maxRow) {
        Pageable pageable = PageRequest.of(page, maxRow, Sort.by("dateCreated").descending());
        Page<Transactions> transactionsPage = transactionRepository.findByDateCreatedBetween(startDate, endDate, pageable);
        return transactionsPage.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private TransactionDTO mapToDTO(Transactions entity) {
        return new TransactionDTO(
                entity.getTransactionId(),
                entity.getCreditAccountNumber(),
                entity.getDebitAccountNumber(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getDateCreated()
        );
    }
}
