package com.example.word_count.service;


import com.example.word_count.model.NodeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendNodeJoin(NodeInfo node) {
        try {
            String json = objectMapper.writeValueAsString(node);
            kafkaTemplate.send("node-join", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize NodeInfo", e);
        }
    }

    public void sendElectionMessage(NodeInfo node) {
        try {
            String json = objectMapper.writeValueAsString(node);
            kafkaTemplate.send("leader-elect", json); // use your election topic
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize election message", e);
        }
    }


    public void sendHeartbeat(NodeInfo node) {
        try {
            String json = objectMapper.writeValueAsString(node);
            kafkaTemplate.send("heartbeat", json);
            log.debug("Sent heartbeat: {}", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize heartbeat", e);
        }
    }

    public void announceLeader(NodeInfo node) {
        try {
            String json = objectMapper.writeValueAsString(node);
            kafkaTemplate.send("leader-elected", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to announce leader", e);
        }
    }
}