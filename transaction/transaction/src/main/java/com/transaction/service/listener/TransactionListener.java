package com.transaction.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.service.dto.AccountResponseDTO;
import com.transaction.service.dto.TopupRequestDTO;
import com.transaction.service.dto.TransactionDTO;
import com.transaction.service.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TransactionListener {

    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    public TransactionListener(TransactionService transactionService, ObjectMapper objectMapper) {
        this.transactionService = transactionService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.create-transaction-req}", containerFactory = "kafkaListenerContainerFactory")
    @SendTo
    public String createTransaction(Map<String, Object> message) {
        try {
            TransactionDTO.RequestTransaction request = objectMapper.convertValue(message, TransactionDTO.RequestTransaction.class);
            transactionService.createTransaction(request);

            AccountResponseDTO response = AccountResponseDTO.builder()
                    .error(false)
                    .message("Transaction processing completed")
                    .build();
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            log.error("Failed to parse transaction request", e);
            try {
                return objectMapper.writeValueAsString(AccountResponseDTO.builder()
                        .error(true)
                        .message("Failed to process transaction: " + e.getMessage())
                        .build());
            } catch (Exception ex) {
                return "{\"error\":true,\"message\":\"Critical error\"}";
            }
        }
    }

    @KafkaListener(topics = "${kafka.topic.get-transaction-req}", containerFactory = "kafkaListenerContainerFactory")
    @SendTo
    public String getTransaction(Map<String, Object> message) {
        try {
            // Assume Gateway sends a Map with DTO fields since it reused TransactionRequestDTO
            String accountNumber = (String) message.get("accountNumber");
            String transactionId = (String) message.get("transactionId");
            Long startDateValue = message.get("startDate") instanceof Number ? ((Number) message.get("startDate")).longValue() : null;
            Long endDateValue = message.get("endDate") instanceof Number ? ((Number) message.get("endDate")).longValue() : null;

            Integer page = message.get("page") != null ? (Integer) message.get("page") : 0;
            Integer maxRow = message.get("maxRow") != null ? (Integer) message.get("maxRow") : 10;

            AccountResponseDTO response = new AccountResponseDTO();
            response.setError(false);
            response.setMessage("Success");

            if (transactionId != null) {
                TransactionDTO detail = transactionService.getTransactionDetail(transactionId);
                response.setResult(detail);
            } else if (accountNumber != null) {
                List<TransactionDTO> list = transactionService.getTransactionsByAccount(accountNumber, page, maxRow);
                response.setListResult((List) list);
            } else if (startDateValue != null && endDateValue != null) {
                java.util.Date startDate = new java.util.Date(startDateValue);
                java.util.Date endDate = new java.util.Date(endDateValue);
                List<TransactionDTO> list = transactionService.getTransactionHistory(startDate, endDate, page, maxRow);
                response.setListResult((List) list);
            } else {
                throw new IllegalArgumentException("Invalid request parameters for getting transactions");
            }

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            log.error("Failed to get transactions", e);
            try {
                return objectMapper.writeValueAsString(AccountResponseDTO.builder()
                        .error(true)
                        .message("Failed to get transactions: " + e.getMessage())
                        .build());
            } catch (Exception ex) {
                return "{\"error\":true,\"message\":\"Critical error\"}";
            }
        }
    }

    @KafkaListener(topics = "${kafka.topic.topup-transaction-req}", containerFactory = "kafkaListenerContainerFactory")
    @SendTo
    public String handleTopupTransaction(Map<String, Object> message) {
        try {
            TopupRequestDTO request = objectMapper.convertValue(message, TopupRequestDTO.class);
            transactionService.topupTransaction(request);

            AccountResponseDTO response = AccountResponseDTO.builder()
                    .error(false)
                    .message("Topup processing completed")
                    .build();
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            log.error("Failed to parse topup request", e);
            try {
                return objectMapper.writeValueAsString(AccountResponseDTO.builder()
                        .error(true)
                        .message("Failed to process topup: " + e.getMessage())
                        .build());
            } catch (Exception ex) {
                return "{\"error\":true,\"message\":\"Critical error\"}";
            }
        }
    }
}
