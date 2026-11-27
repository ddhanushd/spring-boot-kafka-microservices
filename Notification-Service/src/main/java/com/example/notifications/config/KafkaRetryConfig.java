package com.example.notifications.config;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaRetryConfig {
    @Bean
    public KafkaTemplate<String, Object> dltKafkaTemplate(
            KafkaProperties properties) {

        return new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(
                        properties.buildProducerProperties(),
                        new StringSerializer(),
                        new JacksonJsonSerializer<>()
                )
        );
    }

    @Bean
    public DefaultErrorHandler errorHandler(
            KafkaTemplate<String, Object> dltKafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        dltKafkaTemplate,
                        (record, exception) ->
                                new TopicPartition(
                                        record.topic() + ".DLT",
                                        record.partition()
                                )
                );

        return new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(2000L, 3L)
        );
    }
}
