package io.github.ust.mico.loanbroker;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.github.ust.mico.loanbroker.kafka.MicoCloudEventImpl;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Sender {

  @Autowired
  private KafkaTemplate<String, MicoCloudEventImpl<JsonNode>> kafkaTemplate;

  @Value("${kafka.output-topic}")
  private String topic;

  public void send(MicoCloudEventImpl<JsonNode> cloudEvent) {
    send(cloudEvent, topic);
  }

  public void send(MicoCloudEventImpl<JsonNode> cloudEvent, String topic) {
    log.info("sending msg:'{}' to topic:'{}'", cloudEvent, topic);
    kafkaTemplate.send(topic, cloudEvent);
  }
}
