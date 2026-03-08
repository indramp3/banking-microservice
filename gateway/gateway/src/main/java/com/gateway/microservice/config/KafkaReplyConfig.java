package com.gateway.microservice.config;

import com.gateway.microservice.dto.TransactionRequestDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaReplyConfig {

    @Value("${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value("${kafka.consumer.group}")
    private String groupId;

    @Value("${kafka.topic.create-account-resp}")
    private String createAccountRespTopic;

    @Value("${kafka.topic.get-account-resp}")
    private String getAccountRespTopic;

    @Value("${kafka.topic.get-account-balance-resp}")
    private String getBalanceRespTopic;

    @Value("${kafka.topic.create-transaction-resp}")
    private String createTransactionRespTopic;

    @Value("${kafka.topic.get-transaction-resp}")
    private String getTransactionRespTopic;

    @Value("${kafka.topic.topup-transaction-resp}")
    private String topupTransactionRespTopic;

    @Value("${kafka.topic.metrics-acc-resp}")
    private String metricsAccRespTopic;

    @Value("${kafka.topic.health-acc-resp}")
    private String healthAccRespTopic;

    @Value("${kafka.topic.metrics-tx-resp}")
    private String metricsTxRespTopic;

    @Value("${kafka.topic.health-tx-resp}")
    private String healthTxRespTopic;

    @Bean
    public ConsumerFactory<String, String> replyConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> createAccountRepliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {
        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                containerFactory.createContainer(createAccountRespTopic);
        repliesContainer.getContainerProperties().setGroupId(groupId + "-create-account");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> getAccountRepliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {
        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                containerFactory.createContainer(getAccountRespTopic);
        repliesContainer.getContainerProperties().setGroupId(groupId + "-get-account");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> getBalanceRepliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {
        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                containerFactory.createContainer(getBalanceRespTopic);
        repliesContainer.getContainerProperties().setGroupId(groupId + "-get-balance");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(replyConsumerFactory());
        return factory;
    }

    @Bean
    public ReplyingKafkaTemplate<String, Object, String> createAccountRequestReplyKafkaTemplate(
            ProducerFactory<String, Object> producerFactory,
            ConcurrentMessageListenerContainer<String, String> createAccountRepliesContainer) {
        ReplyingKafkaTemplate<String, Object, String> template = new ReplyingKafkaTemplate<>(producerFactory, createAccountRepliesContainer);
        template.setDefaultReplyTimeout(Duration.ofMillis(10000));
        return template;
    }

    @Bean
    public ReplyingKafkaTemplate<String, Object, String> getAccountRequestReplyKafkaTemplate(
            ProducerFactory<String, Object> producerFactory,
            ConcurrentMessageListenerContainer<String, String> getAccountRepliesContainer) {
        ReplyingKafkaTemplate<String, Object, String> template = new ReplyingKafkaTemplate<>(producerFactory, getAccountRepliesContainer);
        template.setDefaultReplyTimeout(Duration.ofMillis(10000));
        return template;
    }

    @Bean
    public ReplyingKafkaTemplate<String, Object, String> getBalanceRequestReplyKafkaTemplate(
            ProducerFactory<String, Object> producerFactory,
            ConcurrentMessageListenerContainer<String, String> getBalanceRepliesContainer) {
        ReplyingKafkaTemplate<String, Object, String> template = new ReplyingKafkaTemplate<>(producerFactory, getBalanceRepliesContainer);
        template.setDefaultReplyTimeout(Duration.ofMillis(10000));
        return template;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> createTransactionRepliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {
        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                containerFactory.createContainer(createTransactionRespTopic);
        repliesContainer.getContainerProperties().setGroupId(groupId + "-create-tx");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public ReplyingKafkaTemplate<String, TransactionRequestDTO.RequestTransaction, String> createTransactionRequestReplyKafkaTemplate(
            ProducerFactory<String, TransactionRequestDTO.RequestTransaction> producerFactory,
            ConcurrentMessageListenerContainer<String, String> createTransactionRepliesContainer) {
        ReplyingKafkaTemplate<String, TransactionRequestDTO.RequestTransaction, String> template =
                new ReplyingKafkaTemplate<>(producerFactory, createTransactionRepliesContainer);
        template.setDefaultReplyTimeout(Duration.ofMillis(10000));
        return template;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> getTransactionRepliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {
        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                containerFactory.createContainer(getTransactionRespTopic);
        repliesContainer.getContainerProperties().setGroupId(groupId + "-get-tx");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public ReplyingKafkaTemplate<String, TransactionRequestDTO, String> getTransactionRequestReplyKafkaTemplate(
            ProducerFactory<String, TransactionRequestDTO> producerFactory,
            ConcurrentMessageListenerContainer<String, String> getTransactionRepliesContainer) {
        ReplyingKafkaTemplate<String, TransactionRequestDTO, String> template =
                new ReplyingKafkaTemplate<>(producerFactory, getTransactionRepliesContainer);
        template.setDefaultReplyTimeout(Duration.ofMillis(10000));
        return template;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> topupTransactionRepliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {
        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                containerFactory.createContainer(topupTransactionRespTopic);
        repliesContainer.getContainerProperties().setGroupId(groupId + "-topup");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public ReplyingKafkaTemplate<String, Object, String> topupTransactionRequestReplyKafkaTemplate(
            ProducerFactory<String, Object> producerFactory,
            ConcurrentMessageListenerContainer<String, String> topupTransactionRepliesContainer) {
        ReplyingKafkaTemplate<String, Object, String> template =
                new ReplyingKafkaTemplate<>(producerFactory, topupTransactionRepliesContainer);
        template.setDefaultReplyTimeout(Duration.ofMillis(10000));
        return template;
    }

    // --- MONITORING CONTAINERS ---
    @Bean
    public ConcurrentMessageListenerContainer<String, String> metricsAccRepliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {
        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                containerFactory.createContainer(metricsAccRespTopic);
        repliesContainer.getContainerProperties().setGroupId(groupId + "-metrics-acc");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> healthAccRepliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {
        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                containerFactory.createContainer(healthAccRespTopic);
        repliesContainer.getContainerProperties().setGroupId(groupId + "-health-acc");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> metricsTxRepliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {
        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                containerFactory.createContainer(metricsTxRespTopic);
        repliesContainer.getContainerProperties().setGroupId(groupId + "-metrics-tx");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> healthTxRepliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {
        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                containerFactory.createContainer(healthTxRespTopic);
        repliesContainer.getContainerProperties().setGroupId(groupId + "-health-tx");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    // --- MONITORING TEMPLATES ---
    @Bean
    public ReplyingKafkaTemplate<String, Object, String> metricsAccReplyingKafkaTemplate(
            ProducerFactory<String, Object> producerFactory,
            ConcurrentMessageListenerContainer<String, String> metricsAccRepliesContainer) {
        ReplyingKafkaTemplate<String, Object, String> template = new ReplyingKafkaTemplate<>(producerFactory, metricsAccRepliesContainer);
        template.setDefaultReplyTimeout(Duration.ofMillis(10000));
        return template;
    }

    @Bean
    public ReplyingKafkaTemplate<String, Object, String> healthAccReplyingKafkaTemplate(
            ProducerFactory<String, Object> producerFactory,
            ConcurrentMessageListenerContainer<String, String> healthAccRepliesContainer) {
        ReplyingKafkaTemplate<String, Object, String> template = new ReplyingKafkaTemplate<>(producerFactory, healthAccRepliesContainer);
        template.setDefaultReplyTimeout(Duration.ofMillis(10000));
        return template;
    }

    @Bean
    public ReplyingKafkaTemplate<String, Object, String> metricsTxReplyingKafkaTemplate(
            ProducerFactory<String, Object> producerFactory,
            ConcurrentMessageListenerContainer<String, String> metricsTxRepliesContainer) {
        ReplyingKafkaTemplate<String, Object, String> template = new ReplyingKafkaTemplate<>(producerFactory, metricsTxRepliesContainer);
        template.setDefaultReplyTimeout(Duration.ofMillis(10000));
        return template;
    }

    @Bean
    public ReplyingKafkaTemplate<String, Object, String> healthTxReplyingKafkaTemplate(
            ProducerFactory<String, Object> producerFactory,
            ConcurrentMessageListenerContainer<String, String> healthTxRepliesContainer) {
        ReplyingKafkaTemplate<String, Object, String> template = new ReplyingKafkaTemplate<>(producerFactory, healthTxRepliesContainer);
        template.setDefaultReplyTimeout(Duration.ofMillis(10000));
        return template;
    }
}
