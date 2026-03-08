package com.gateway.microservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.microservice.model.BaseResponse;
import com.gateway.microservice.model.TopupRequestDTO;
import com.gateway.microservice.model.TransactionRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Value("${kafka.topic.create-transaction-req}")
    private String requestReplyTopicCreateTransaction;

    @Value("${kafka.topic.get-transaction-req}")
    private String requestReplyTopicGetTransaction;

    @Value("${kafka.topic.topup-transaction-req}")
    private String requestReplyTopicTopupTransaction;

    private final ReplyingKafkaTemplate<String, TransactionRequestDTO.RequestTransaction, String> createTransactionRequestReplyKafkaTemplate;
    private final ReplyingKafkaTemplate<String, TransactionRequestDTO, String> getTransactionRequestReplyKafkaTemplate;
    private final ReplyingKafkaTemplate<String, Object, String> topupTransactionRequestReplyKafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public TransactionController(
            ReplyingKafkaTemplate<String, TransactionRequestDTO.RequestTransaction, String> createTransactionRequestReplyKafkaTemplate,
            ReplyingKafkaTemplate<String, TransactionRequestDTO, String> getTransactionRequestReplyKafkaTemplate,
            ReplyingKafkaTemplate<String, Object, String> topupTransactionRequestReplyKafkaTemplate,
            ObjectMapper objectMapper)
    {
        this.createTransactionRequestReplyKafkaTemplate = createTransactionRequestReplyKafkaTemplate;
        this.getTransactionRequestReplyKafkaTemplate = getTransactionRequestReplyKafkaTemplate;
        this.topupTransactionRequestReplyKafkaTemplate = topupTransactionRequestReplyKafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createTransaction(
            @RequestBody TransactionRequestDTO.RequestTransaction request
    ) throws ExecutionException, InterruptedException, TimeoutException, JsonProcessingException {
        log.info("Create New Transaction");

        ProducerRecord<String, TransactionRequestDTO.RequestTransaction> producerRecord = new ProducerRecord<>(requestReplyTopicCreateTransaction, request);
        RequestReplyFuture<String, TransactionRequestDTO.RequestTransaction, String> future = createTransactionRequestReplyKafkaTemplate.sendAndReceive(producerRecord);
        ConsumerRecord<String, String> consumerRecord = future.get(5, TimeUnit.MINUTES);
        return ok().body(new ObjectMapper().readValue(consumerRecord.value(), BaseResponse.class));
    }

    @GetMapping("/account")
    public ResponseEntity<BaseResponse> getTransaction(
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam("page") Integer page,
            @RequestParam("maxRow") Integer maxRow
    ) throws ExecutionException, InterruptedException, TimeoutException, JsonProcessingException {
        log.info("Get Transaction");

        if (accountNumber == null) throw new IllegalArgumentException("Account Number is required");
        if (!accountNumber.matches("\\d+")) throw new IllegalArgumentException("Account Number must be a number");
        if (page == null) page = 1;
        if (maxRow == null) maxRow = 10;

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountNumber(accountNumber);
        request.setPage(page);
        request.setMaxRow(maxRow);

        ProducerRecord<String, TransactionRequestDTO> producerRecord = new ProducerRecord<>(requestReplyTopicGetTransaction, request);
        RequestReplyFuture<String, TransactionRequestDTO, String> future = getTransactionRequestReplyKafkaTemplate.sendAndReceive(producerRecord);
        ConsumerRecord<String, String> consumerRecord = future.get(5, TimeUnit.MINUTES);
        return ok().body(new ObjectMapper().readValue(consumerRecord.value(), BaseResponse.class));
    }

    @GetMapping("/detail")
    public ResponseEntity<BaseResponse> getTransactionDetail(
            @RequestParam("transactionId") String transactionId
    ) throws ExecutionException, InterruptedException, TimeoutException, JsonProcessingException {
        log.info("Get Transaction Detail");

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setTransactionId(transactionId);

        if (transactionId == null) throw new IllegalArgumentException("Transaction ID is required");
        ProducerRecord<String, TransactionRequestDTO> producerRecord = new ProducerRecord<>(requestReplyTopicGetTransaction, request);
        RequestReplyFuture<String, TransactionRequestDTO, String> future = getTransactionRequestReplyKafkaTemplate.sendAndReceive(producerRecord);
        ConsumerRecord<String, String> consumerRecord = future.get(5, TimeUnit.MINUTES);
        return ok().body(new ObjectMapper().readValue(consumerRecord.value(), BaseResponse.class));
    }

    @GetMapping("/history")
    public ResponseEntity<BaseResponse> getTransactionHistory(
            @RequestParam("startDate") Date startDate,
            @RequestParam("endDate") Date endDate,
            @RequestParam("page") Integer page,
            @RequestParam("maxRow") Integer maxRow
    ) throws ExecutionException, InterruptedException, TimeoutException, JsonProcessingException {
        log.info("Get Transaction History");

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setPage(page);
        request.setMaxRow(maxRow);

        ProducerRecord<String, TransactionRequestDTO> producerRecord = new ProducerRecord<>(requestReplyTopicGetTransaction, request);
        RequestReplyFuture<String, TransactionRequestDTO, String> future = getTransactionRequestReplyKafkaTemplate.sendAndReceive(producerRecord);
        ConsumerRecord<String, String> consumerRecord = future.get(5, TimeUnit.MINUTES);
        return ok().body(new ObjectMapper().readValue(consumerRecord.value(), BaseResponse.class));
    }

    @PostMapping("/topup")
    public ResponseEntity<BaseResponse> topupTransaction(
            @RequestBody TopupRequestDTO requestDetail) {
        log.info("Sending request to topup account: {}", requestDetail.getAccountNumber());
        try {
            ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(requestReplyTopicTopupTransaction, requestDetail);
            RequestReplyFuture<String, Object, String> replyFuture = topupTransactionRequestReplyKafkaTemplate.sendAndReceive(producerRecord);
            ConsumerRecord<String, String> consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);

            BaseResponse responseDTO = objectMapper.readValue(consumerRecord.value(), BaseResponse.class);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        } catch (Exception e) {
            BaseResponse responseDTO = new BaseResponse();
            responseDTO.setCode("500");
            responseDTO.setMessage("Gateway timeout or error processing topup");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}
