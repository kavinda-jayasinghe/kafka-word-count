package com.example.word_count.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic nodeJoinTopic() {
        return new NewTopic("node-join", 1, (short) 1);
    }

    @Bean
    public NewTopic nodeSyncTopic() {
        return new NewTopic("node-sync", 1, (short) 1);
    }

    @Bean
    public NewTopic heartbeatTopic() {
        return new NewTopic("heartbeat", 1, (short) 1);
    }

}