package com.gateway.microservice.producer;

import com.gateway.microservice.dto.GetAccountDTO;
import com.gateway.microservice.model.TransactionRequestDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configurable
public class GetTransactionAccountKafkaProducer {

    @Value( value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${kafka.consumer.group}")
    private String groupId;

    @Value(value = "${kafka.topic.get-transaction-resp}")
    private String topicResponse;

    @Bean
    public ReplyingKafkaTemplate<String, GetAccountDTO.Request, String> getTransactionRequestReplyKafkaTemplate(
            ConcurrentKafkaListenerContainerFactory<String, TransactionRequestDTO.RequestTransaction> kafkaListenerContainerFactory
    ) {
        kafkaListenerContainerFactory.setReplyTemplate(registerKafkaTemplate(registerProducerFactory()));
        ConcurrentMessageListenerContainer<String, TransactionRequestDTO.RequestTransaction> container = registerReplyContainer(kafkaListenerContainerFactory);
        return (ReplyingKafkaTemplate<String, GetAccountDTO.Request, String>) new ReplyingKafkaTemplate(registerProducerFactory(), container);
    }

    public KafkaTemplate<String, TransactionRequestDTO.RequestTransaction> registerKafkaTemplate(ProducerFactory<String, TransactionRequestDTO.RequestTransaction> pf) {
        return new KafkaTemplate<>(pf);
    }

    public ProducerFactory<String, TransactionRequestDTO.RequestTransaction> registerProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    public ConcurrentMessageListenerContainer<String, TransactionRequestDTO.RequestTransaction> registerReplyContainer(
            ConcurrentKafkaListenerContainerFactory<String, TransactionRequestDTO.RequestTransaction> containerFactory
    ) {
        ConcurrentMessageListenerContainer<String, TransactionRequestDTO.RequestTransaction>
                container = containerFactory.createContainer(topicResponse);
        container.getContainerProperties().setGroupId(groupId);
        return container;
    }
}
