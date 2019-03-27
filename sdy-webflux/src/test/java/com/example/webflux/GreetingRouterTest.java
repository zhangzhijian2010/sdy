package com.example.webflux;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/26.
 */
@RunWith(SpringRunner.class)
//  We create a `@SpringBootTest`, starting an actual server on a `RANDOM_PORT`
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GreetingRouterTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testHello(){
        webTestClient.get().uri("/hello").accept(MediaType.TEXT_PLAIN)
                .exchange().expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello, CityXX!");
    }

}
