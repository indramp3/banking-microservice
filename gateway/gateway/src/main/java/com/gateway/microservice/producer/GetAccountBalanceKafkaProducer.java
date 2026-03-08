package com.gateway.microservice.producer;

import com.gateway.microservice.dto.GetAccountDTO;
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
public class GetAccountBalanceKafkaProducer {

    @Value( value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${kafka.consumer.group}")
    private String groupId;

    @Value(value = "${kafka.topic.get-account-balance-resp}")
    private String topicResponse;

    @Bean
    public ReplyingKafkaTemplate<String, GetAccountDTO.Request, String> getBalanceRequestReplyKafkaTemplate(
            ConcurrentKafkaListenerContainerFactory<String, GetAccountDTO.Request> kafkaListenerContainerFactory
    ) {
        kafkaListenerContainerFactory.setReplyTemplate(registerKafkaTemplate(registerProducerFactory()));
        ConcurrentMessageListenerContainer<String, GetAccountDTO.Request> container = registerReplyContainer(kafkaListenerContainerFactory);
        return (ReplyingKafkaTemplate<String, GetAccountDTO.Request, String>) new ReplyingKafkaTemplate(registerProducerFactory(), container);
    }

    private KafkaTemplate<String, GetAccountDTO.Request> registerKafkaTemplate(ProducerFactory<String, GetAccountDTO.Request> pf) {
        return new KafkaTemplate<>(pf);
    }

    public ProducerFactory<String, GetAccountDTO.Request> registerProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    public ConcurrentMessageListenerContainer<String, GetAccountDTO.Request> registerReplyContainer(
            ConcurrentKafkaListenerContainerFactory<String, GetAccountDTO.Request> containerFactory
    ) {
        ConcurrentMessageListenerContainer<String, GetAccountDTO.Request>
                container = containerFactory.createContainer(topicResponse);
        container.getContainerProperties().setGroupId(groupId);
        return container;
    }
}
