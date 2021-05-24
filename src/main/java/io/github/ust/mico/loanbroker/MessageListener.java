package io.github.ust.mico.loanbroker;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import io.github.ust.mico.loanbroker.kafka.MicoCloudEventImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageListener {

    /**
     * Used for messaging via stomp and websockets
     */
    @Autowired
    SimpMessagingTemplate websocketsTemplate;

    @Autowired
    private Service service;

    /**
     * Entry point for incoming messages from kafka.
     *
     * @param message
     */
    @KafkaListener(topics = "${kafka.input-topic}", groupId = "${kafka.group-id}")
    public void receive(MicoCloudEventImpl<JsonNode> cloudEvent) {
        log.info("Received CloudEvent message: {}", cloudEvent);
        // websocketsTemplate.convertAndSend("/topic/messaging-bridge", cloudEvent);
        service.processMessage(cloudEvent);
    }

}
