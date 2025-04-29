package com.example.word_count.service;


import com.example.word_count.model.NodeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderElectionService {
    private final KafkaProducerService kafkaProducerService;
    private final Set<NodeInfo> nodes = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper;

    @Value("${server.port}")
    private String port;

    @Value("${node.value}")
    private int nodeId;
    private int nodeCount = 0;
    private volatile boolean electionStarted = false;
    private volatile boolean isCoordinator = false;
    private NodeInfo thisNode;
    private boolean isLeaderSelect=false;

    @PostConstruct
    public void registerSelf() {
        thisNode = new NodeInfo(nodeId, port);
        nodes.add(thisNode);
        kafkaProducerService.sendNodeJoin(thisNode);
    }

    @KafkaListener(topics = "node-join", groupId = "cluster-group")
    public void handleNodeJoin(NodeInfo newNode) {
        nodes.add(newNode);
        log.info("[NEW NODE JOINED] NodeId={}, Port={}", newNode.getNodeId(), newNode.getPort());
    }

    @KafkaListener(topics = "leader-elected", groupId = "cluster-group")
    public void handleLeaderElect(NodeInfo newNode) {
        if (isLeaderSelect){
            log.info("[------Leader is elect-----] NodeId={}, Port={}", newNode.getNodeId(), newNode.getPort());
        }
    }

    @Scheduled(fixedRate = 1000)
    public void checkNodeCount() {
        if (nodeCount < nodes.size()) {
            log.info("[NEW NODE JOINED]");
            nodeCount++;
            if (nodes.size()>=2){
                startLeaderElection();
            }
        }

    }

    private void startLeaderElection() {
        log.info("=============================leader election");
        int myId = thisNode.getNodeId();
        boolean higherNodeExists = false;

        for (NodeInfo node : nodes) {
            if (node.getNodeId() > myId) {
                kafkaProducerService.sendElectionMessage(node);
                higherNodeExists = true;
            }
        }

        if (!higherNodeExists) {
            becomeLeader();
        } else {
            log.info("[WAITING FOR HIGHER NODE TO RESPOND]");
        }
    }

    private void becomeLeader() {
        isCoordinator = true;
        log.info("[BECOMING LEADER] NodeId={}", thisNode.getNodeId());
        kafkaProducerService.announceLeader(thisNode);
    }

}