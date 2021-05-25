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
package io.github.ust.mico.loanbroker.bank;

import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.ust.mico.loanbroker.kafka.MicoCloudEventImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Bank {

  private double computeInterestRate(int loanTerm) {
    return (double) (loanTerm / 12) / 10 + (double) new Random().ints(0, 10).findFirst().getAsInt();
  }

  public MicoCloudEventImpl<JsonNode> processMessage(MicoCloudEventImpl<JsonNode> cloudEvent) {
    ObjectNode data = (ObjectNode) cloudEvent.getData().orElse(null);
    log.info("Received request for SSN '{}', for {} / {} months", data.get("SSN"), data.get("loan"), data.get("term"));
    double interestRate = computeInterestRate(data.get("term").asInt(0));
    data.put("interestRate", interestRate);
    cloudEvent.setData(data);
    return cloudEvent;
  }
}
