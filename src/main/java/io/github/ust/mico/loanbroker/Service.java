/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.github.ust.mico.loanbroker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import io.github.ust.mico.loanbroker.bank.Bank;
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

    String incomingCloudEventString = cloudEvent.toString();

    // Get return address to where the processed message should be sent back.
    String returnAddress = cloudEvent.getReturnTopic().orElse("");
    // Set correlationid to current (msg) id.
    cloudEvent.setCorrelationId(cloudEvent.getId());
    // And create and set a new (msg) id.
    cloudEvent.setRandomId();

    // Set content
    // ObjectNode node = JsonNodeFactory.instance.objectNode();
    // node.put("service", "processed");
    // outMsg.setData(node);

    cloudEvent = new Bank().processMessage(cloudEvent);

    websocketsTemplate.convertAndSend("/topic/messaging-bridge", JsonNodeFactory.instance.objectNode()
        .put("incoming", incomingCloudEventString).put("outgoing", cloudEvent.toString()));

    sender.send(cloudEvent, returnAddress);
  }

}
