package com.example.demokafkaconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.function.Predicate.not;

@SpringBootApplication
@RestController
public class DemoKafkaConsumerApplication {

	private final Sinks.Many<String> sink = Sinks.many().replay().limit(1000);

	private final Logger log = LoggerFactory.getLogger(DemoKafkaConsumerApplication.class);

	private final KafkaListenerEndpointRegistry endpointRegistry;

	public DemoKafkaConsumerApplication(KafkaListenerEndpointRegistry endpointRegistry) {
		this.endpointRegistry = endpointRegistry;
	}

	@GetMapping(path = "/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> messages() {
		this.endpointRegistry.getListenerContainers()
				.stream()
				.filter(not(MessageListenerContainer::isRunning))
				.forEach(MessageListenerContainer::start);
		return this.sink.asFlux();
	}

	@KafkaListener(topics = "${sample.topic}", autoStartup = "false")
	public void onMessage(String message) {
		log.info("onMessage({})", message);
		this.sink.tryEmitNext(message);
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoKafkaConsumerApplication.class, args);
	}
}
