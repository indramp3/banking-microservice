package com.acount.service.consumer;

import com.acount.service.dto.AccountMasterDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

public class CreateAccountConsumer extends BaseKafkaConsumer<AccountMasterDTO>{

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
