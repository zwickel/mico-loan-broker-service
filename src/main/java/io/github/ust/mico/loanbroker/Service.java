package io.github.ust.mico.loanbroker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import io.github.ust.mico.loanbroker.kafka.MicoCloudEventImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Service {

  /**
   * Used for messaging via stomp and websockets
   */
  @Autowired
  SimpMessagingTemplate websocketsTemplate;

  @Autowired
  private Sender sender;

  public void processMessage(MicoCloudEventImpl<JsonNode> cloudEvent) {
    log.info("Input message to process: '{}'", cloudEvent);

    MicoCloudEventImpl<JsonNode> outMsg = new MicoCloudEventImpl<JsonNode>(cloudEvent);

    // Get return address to where the processed message should be sent back.
    String returnAddress = outMsg.getReturnTopic().orElse("");
    // Set correlationid to current (msg) id.
    outMsg.setCorrelationId(outMsg.getId());
    // And create and set a new (msg) id.
    outMsg.setRandomId();

    // Set content
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    node.put("service", "processed");
    outMsg.setData(node);

    websocketsTemplate.convertAndSend("/topic/messaging-bridge", JsonNodeFactory.instance.objectNode()
        .put("incoming", cloudEvent.toString()).put("outgoing", outMsg.toString()));

    sender.send(outMsg, returnAddress);
  }

}
