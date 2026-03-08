package com.gateway.microservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.microservice.dto.GetAccountDTO;
import com.gateway.microservice.model.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    @Value("${kafka.topic.add-account-req}")
    private String createAccountTopicRequest;

    @Value("${kafka.topic.get-account-req}")
    private String getAccountTopicRequest;

    @Value("${kafka.topic.get-account-balance-req}")
    private String getBalanceTopicRequest;

    private final ReplyingKafkaTemplate<String, Object, String> createAccountRequestReplyKafkaTemplate;
    private final ReplyingKafkaTemplate<String, Object, String> getAccountRequestReplyKafkaTemplate;
    private final ReplyingKafkaTemplate<String, Object, String> getBalanceRequestReplyKafkaTemplate;

    @Autowired
    public AccountController(ReplyingKafkaTemplate<String, Object, String> createAccountRequestReplyKafkaTemplate,
                             ReplyingKafkaTemplate<String, Object, String> getAccountRequestReplyKafkaTemplate,
                             ReplyingKafkaTemplate<String, Object, String> getBalanceRequestReplyKafkaTemplate) {
        this.createAccountRequestReplyKafkaTemplate = createAccountRequestReplyKafkaTemplate;
        this.getAccountRequestReplyKafkaTemplate = getAccountRequestReplyKafkaTemplate;
        this.getBalanceRequestReplyKafkaTemplate = getBalanceRequestReplyKafkaTemplate;
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse> addAccount(
            @RequestBody com.gateway.microservice.dto.CreateAccountRequestDTO request
    ) throws ExecutionException, InterruptedException, TimeoutException, JsonProcessingException {
        log.info("Create New Account");
        ProducerRecord<String, Object> pr = new ProducerRecord<>(createAccountTopicRequest, request);
        RequestReplyFuture<String, Object, String> future = createAccountRequestReplyKafkaTemplate.sendAndReceive(pr);
        ConsumerRecord<String, String> consumerRecord = future.get(5, TimeUnit.MINUTES);
        return ok().body(new ObjectMapper().readValue(consumerRecord.value(), BaseResponse.class));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse> getAccount(
            @RequestParam("nik") Integer nik,
            @RequestParam("accountNumber") Integer accountNumber,
            @RequestParam("page") Integer page,
            @RequestParam("maxRow") Integer maxRow
    ) throws ExecutionException, InterruptedException, TimeoutException, JsonProcessingException {
        log.info("Get Account");

        if (page == null || page < 1) page = 1;
        if (maxRow == null) maxRow = 10;

        GetAccountDTO.Request request = new GetAccountDTO.Request();
        request.setNik(nik);
        request.setAccountNumber(accountNumber);
        request.setPage(page - 1);
        request.setMaxRow(maxRow);

        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(getAccountTopicRequest, request);
        RequestReplyFuture<String, Object, String> future = getAccountRequestReplyKafkaTemplate.sendAndReceive(producerRecord);
        ConsumerRecord<String, String> consumerRecord = future.get(5, TimeUnit.MINUTES);
        return ok().body(new ObjectMapper().readValue(consumerRecord.value(), BaseResponse.class));
    }

    @GetMapping("/balance")
    public ResponseEntity<BaseResponse> getBalance(
            @RequestParam("accountNumber") Integer accountNumber
    ) throws ExecutionException, InterruptedException, TimeoutException, JsonProcessingException {
        log.info("Get Balance");

        if (accountNumber == null) throw new IllegalArgumentException("Account Number is required");
        GetAccountDTO.Request request = new GetAccountDTO.Request();
        request.setAccountNumber(accountNumber);

        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(getBalanceTopicRequest, request);
        RequestReplyFuture<String, Object, String> future = getBalanceRequestReplyKafkaTemplate.sendAndReceive(producerRecord);
        ConsumerRecord<String, String> consumerRecord = future.get(5, TimeUnit.MINUTES);
        return ok().body(new ObjectMapper().readValue(consumerRecord.value(), BaseResponse.class));
    }
}
