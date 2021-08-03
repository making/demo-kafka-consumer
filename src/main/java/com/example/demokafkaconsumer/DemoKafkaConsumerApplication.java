package com.example.demokafkaconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoKafkaConsumerApplication {

	private final Sinks.Many<String> sink = Sinks.many().replay().limit(1000);

	private final Logger log = LoggerFactory.getLogger(DemoKafkaConsumerApplication.class);

	@GetMapping(path = "/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> messages() {
		return this.sink.asFlux();
	}

	@KafkaListener(topics = "${sample.topic}")
	public void onMessage(String message) {
		log.info("onMessage({})", message);
		this.sink.tryEmitNext(message);
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoKafkaConsumerApplication.class, args);
	}
}
