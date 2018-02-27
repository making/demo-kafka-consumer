package com.example.demokafkaconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

@SpringBootApplication
@RestController
public class DemoKafkaConsumerApplication {

    private final UnicastProcessor<String> hotSource = UnicastProcessor.create();
    private final Flux<String> messages = hotSource.publish()
            .autoConnect().share();


    @GetMapping(path = "/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> messages() {
        return this.messages.log("messages");
    }

    @KafkaListener(topics = "${sample.topic}")
    public void onMessage(String message) {
        this.hotSource.onNext(message);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoKafkaConsumerApplication.class, args);
    }
}
