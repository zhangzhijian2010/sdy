package com.example.webflux.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;


/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/27.
 */
@Component
public class TimerHandler {

    public Mono<ServerResponse> getTime(ServerRequest request) {
        return ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("Nos is " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))), String.class);
    }

    public Mono<ServerResponse> getDate(ServerRequest request) {
        return ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("Nos is " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))), String.class);
    }

    public Mono<ServerResponse> sendTimePerSec(ServerRequest request) {
        return ok().contentType(MediaType.TEXT_EVENT_STREAM).body(Flux.interval(Duration.ofSeconds(1)).map(l -> "Nos is " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))), String.class);
    }
}
