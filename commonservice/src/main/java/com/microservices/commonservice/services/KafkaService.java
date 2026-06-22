package com.microservices.commonservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaService {

    private final KafkaTemplate<String,String> kafkaTemplate;

    public KafkaService(KafkaTemplate<String,String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic,String message){
        kafkaTemplate.send(topic,message);
        log.info("Message send to topic: " + topic);
    }
}