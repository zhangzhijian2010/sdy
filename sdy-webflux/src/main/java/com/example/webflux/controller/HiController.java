package com.example.webflux.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/26.
 */
@RestController
public class HiController {

    @GetMapping("/hi")
    public Mono<String> hello() {   // 【改】返回类型为Mono<String>
        return Mono.just("Welcome to reactive world ~");     // 【改】使用Mono.just生成响应式数据
    }

    @GetMapping("/hi2")
    public Mono<String> hi2() throws InterruptedException {   // 【改】返回类型为Mono<String>
        Mono<String> s=  Mono.just(helll());
        System.out.println("xxxx");
        return s;
    }

    public String helll() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2l);
        return "xxx";
    }
}