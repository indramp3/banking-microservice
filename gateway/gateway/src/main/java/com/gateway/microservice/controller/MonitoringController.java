package com.gateway.microservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.microservice.model.BaseResponse;
import com.gateway.microservice.model.monitoring.MonitoringDTOs;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    @Value("${kafka.topic.metrics-acc-req}")
    private String metricsAccReqTopic;

    @Value("${kafka.topic.health-acc-req}")
    private String healthAccReqTopic;

    @Value("${kafka.topic.metrics-tx-req}")
    private String metricsTxReqTopic;

    @Value("${kafka.topic.health-tx-req}")
    private String healthTxReqTopic;

    private final ReplyingKafkaTemplate<String, Object, String> metricsAccReplyingKafkaTemplate;
    private final ReplyingKafkaTemplate<String, Object, String> healthAccReplyingKafkaTemplate;
    private final ReplyingKafkaTemplate<String, Object, String> metricsTxReplyingKafkaTemplate;
    private final ReplyingKafkaTemplate<String, Object, String> healthTxReplyingKafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public MonitoringController(
            ReplyingKafkaTemplate<String, Object, String> metricsAccReplyingKafkaTemplate,
            ReplyingKafkaTemplate<String, Object, String> healthAccReplyingKafkaTemplate,
            ReplyingKafkaTemplate<String, Object, String> metricsTxReplyingKafkaTemplate,
            ReplyingKafkaTemplate<String, Object, String> healthTxReplyingKafkaTemplate,
            ObjectMapper objectMapper) {
        this.metricsAccReplyingKafkaTemplate = metricsAccReplyingKafkaTemplate;
        this.healthAccReplyingKafkaTemplate = healthAccReplyingKafkaTemplate;
        this.metricsTxReplyingKafkaTemplate = metricsTxReplyingKafkaTemplate;
        this.healthTxReplyingKafkaTemplate = healthTxReplyingKafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/metrics")
    public ResponseEntity<BaseResponse> getBusinessMetrics() {
        log.info("Requesting Business Metrics across services");
        BaseResponse response = new BaseResponse();

        try {
            // Future 1: Account Metrics
            ProducerRecord<String, Object> accRecord = new ProducerRecord<>(metricsAccReqTopic, new Object());
            RequestReplyFuture<String, Object, String> accFuture = metricsAccReplyingKafkaTemplate.sendAndReceive(accRecord);

            // Future 2: Transaction Metrics
            ProducerRecord<String, Object> txRecord = new ProducerRecord<>(metricsTxReqTopic, new Object());
            RequestReplyFuture<String, Object, String> txFuture = metricsTxReplyingKafkaTemplate.sendAndReceive(txRecord);

            // Wait and Resolve
            ConsumerRecord<String, String> accConsumerRecord = accFuture.get(10, TimeUnit.SECONDS);
            ConsumerRecord<String, String> txConsumerRecord = txFuture.get(10, TimeUnit.SECONDS);

            MonitoringDTOs.AccountMetrics accMetrics = objectMapper.readValue(accConsumerRecord.value(), MonitoringDTOs.AccountMetrics.class);
            MonitoringDTOs.TransactionMetrics txMetrics = objectMapper.readValue(txConsumerRecord.value(), MonitoringDTOs.TransactionMetrics.class);

            MonitoringDTOs.CombinedMetricsResponse combined = new MonitoringDTOs.CombinedMetricsResponse(accMetrics, txMetrics);

            response.setCode("200");
            response.setMessage("Business metrics successfully retrieved");
            response.setData(combined);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to gather business metrics", e);
            response.setCode("500");
            response.setMessage("Failed to aggregate metrics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<BaseResponse> getSystemHealth() {
        log.info("Pinging microservices for health check");
        BaseResponse response = new BaseResponse();

        try {
            long currentTimestamp = System.currentTimeMillis();
            MonitoringDTOs.HealthPing ping = new MonitoringDTOs.HealthPing(currentTimestamp);

            // Ping Account
            ProducerRecord<String, Object> accRecord = new ProducerRecord<>(healthAccReqTopic, ping);
            RequestReplyFuture<String, Object, String> accFuture = healthAccReplyingKafkaTemplate.sendAndReceive(accRecord);

            // Ping Transaction
            ProducerRecord<String, Object> txRecord = new ProducerRecord<>(healthTxReqTopic, ping);
            RequestReplyFuture<String, Object, String> txFuture = healthTxReplyingKafkaTemplate.sendAndReceive(txRecord);

            // Wait and Resolve
            ConsumerRecord<String, String> accConsumerRecord = accFuture.get(5, TimeUnit.SECONDS);
            ConsumerRecord<String, String> txConsumerRecord = txFuture.get(5, TimeUnit.SECONDS);

            MonitoringDTOs.HealthResponse accHealth = objectMapper.readValue(accConsumerRecord.value(), MonitoringDTOs.HealthResponse.class);
            MonitoringDTOs.HealthResponse txHealth = objectMapper.readValue(txConsumerRecord.value(), MonitoringDTOs.HealthResponse.class);

            // Calculate Latency Backoff
            accHealth.setLatencyMs(accHealth.getTimestampReceived() - ping.getTimestampSent());
            txHealth.setLatencyMs(txHealth.getTimestampReceived() - ping.getTimestampSent());

            MonitoringDTOs.CombinedHealthResponse combined = new MonitoringDTOs.CombinedHealthResponse(accHealth, txHealth);

            response.setCode("200");
            response.setMessage("System is operational");
            response.setData(combined);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to gather system health ping", e);
            response.setCode("500");
            response.setMessage("One or more services did not respond: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
