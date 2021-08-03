package com.example.demokafkaconsumer;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverOptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoKafkaConsumerApplication {
	private final ReactiveKafkaConsumerTemplate<Object, String> kafkaTemplate;

	public DemoKafkaConsumerApplication(@Value("${sample.topic}") String topic, KafkaProperties properties) {
		this.kafkaTemplate = new ReactiveKafkaConsumerTemplate<>(ReceiverOptions.<Object, String>create(properties.buildConsumerProperties())
				.subscription(List.of(topic)));
	}

	private final Logger log = LoggerFactory.getLogger(DemoKafkaConsumerApplication.class);

	@GetMapping(path = "/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> messages() {
		return this.kafkaTemplate
				.receiveAutoAck()
				.doOnRequest(n -> log.info("request({})", n))
				.map(ConsumerRecord::value);
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoKafkaConsumerApplication.class, args);
	}
}
