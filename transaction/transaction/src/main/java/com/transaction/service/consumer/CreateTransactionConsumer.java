package com.transaction.service.consumer;

import com.transaction.service.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@EnableKafka
@Configuration
public class CreateTransactionConsumer extends BaseKafkaConsumer<TransactionDTO.RequestTransaction>{

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${kafka.consumer.group}")
    private String groupId;

    @Override
    protected String getBootstrapAddr() {
        return bootstrapAddress;
    }

    @Override
    protected String getGroupId() {
        return groupId;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
    insertItemKafkaListenerContainerFactory(){
        return this.getKafkaListenerContainerFactory();
    }
}
