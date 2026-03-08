package com.acount.service.listener;

import com.acount.service.dto.AccountMasterDTO;
import com.acount.service.entity.AccountMaster;
import com.acount.service.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.acount.service.dto.TransferRequestDTO;

@Slf4j
public class AccountListener {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;


    public AccountListener(AccountService accountService, ObjectMapper objectMapper) {
        this.accountService = accountService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.create-account-req}", containerFactory = "kafkaListenerContainerFactory")
    @SendTo
    public String createAccount(Map<String, Object> message) {
        try {
            AccountMasterDTO.RequestCreateAccount requestCreateAccount =
                    objectMapper.convertValue(message, AccountMasterDTO.RequestCreateAccount.class);
            AccountMasterDTO.ResponseCreateAccount response = accountService.createNewAccount(requestCreateAccount);
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            log.error("Failed to create account from message", e);
            try {
                return objectMapper.writeValueAsString(AccountMasterDTO.ResponseCreateAccount.builder()
                        .error(true)
                        .message("Failed to insert new account: " + e.getMessage())
                        .result(null)
                        .listResult(null)
                        .build());
            } catch (Exception ex) {
                return "{\"error\":true,\"message\":\"Failed to process error response\"}";
            }
        }
    }

    @KafkaListener(topics = "${kafka.topic.get-account-req}", containerFactory = "kafkaListenerContainerFactory")
    @SendTo
    public String getAccount(Map<String, Object> message) {
        try {
            AccountMasterDTO request = objectMapper.convertValue(message, AccountMasterDTO.class);
            List<AccountMaster> accounts = accountService.getByNikAndAccountNumber(request);

            List<AccountMasterDTO.DataResp> dataResps = new ArrayList<>();
            for (AccountMaster am : accounts) {
                dataResps.add(AccountMasterDTO.DataResp.builder()
                        .id(null)
                        .name(am.getCustomerName())
                        .price(am.getBalance())
                        .build());
            }

            AccountMasterDTO.ResponseCreateAccount response = AccountMasterDTO.ResponseCreateAccount.builder()
                    .error(false)
                    .message("Success fetching accounts")
                    .listResult(dataResps)
                    .build();
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            log.error("Failed to get account details", e);
            try {
                return objectMapper.writeValueAsString(AccountMasterDTO.ResponseCreateAccount.builder()
                        .error(true)
                        .message("Failed to get account: " + e.getMessage())
                        .build());
            } catch (Exception ex) {
                return "{\"error\":true,\"message\":\"Failed to process error response\"}";
            }
        }
    }

    @KafkaListener(topics = "${kafka.topic.get-account-balance-req}", containerFactory = "kafkaListenerContainerFactory")
    @SendTo
    public String getBalance(Map<String, Object> message) {
        try {
            AccountMasterDTO request = objectMapper.convertValue(message, AccountMasterDTO.class);
            List<AccountMaster> accounts = accountService.getByNikAndAccountNumber(request);

            BigDecimal balance = BigDecimal.ZERO;
            if (!accounts.isEmpty()) {
                balance = accounts.get(0).getBalance();
            }

            AccountMasterDTO.DataResp dataResp = AccountMasterDTO.DataResp.builder()
                    .price(balance)
                    .build();

            AccountMasterDTO.ResponseCreateAccount response = AccountMasterDTO.ResponseCreateAccount.builder()
                    .error(false)
                    .message("Success fetching balance")
                    .result(dataResp)
                    .build();

            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            log.error("Failed to get balance", e);
            try {
                return objectMapper.writeValueAsString(AccountMasterDTO.ResponseCreateAccount.builder()
                        .error(true)
                        .message("Failed to get balance: " + e.getMessage())
                        .build());
            } catch (Exception ex) {
                return "{\"error\":true,\"message\":\"Failed to process error response\"}";
            }
        }
    }

    @KafkaListener(topics = "${kafka.topic.update-balance-req}", containerFactory = "kafkaListenerContainerFactory")
    @SendTo
    public String updateBalance(Map<String, Object> message) {
        try {
            TransferRequestDTO transferRequest = objectMapper.convertValue(message, TransferRequestDTO.class);
            AccountMasterDTO.ResponseCreateAccount response = accountService.processTransfer(transferRequest);
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            log.error("Failed to update balance", e);
            try {
                return objectMapper.writeValueAsString(AccountMasterDTO.ResponseCreateAccount.builder()
                        .error(true)
                        .message("Failed to update balance: " + e.getMessage())
                        .build());
            } catch (Exception ex) {
                return "{\"error\":true,\"message\":\"Failed to process error response\"}";
            }
        }
    }
}
