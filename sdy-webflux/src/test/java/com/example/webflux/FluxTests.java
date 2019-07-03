package com.example.webflux;

import org.junit.Test;
import reactor.core.publisher.Flux;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/27.
 */
public class FluxTests {

    @Test
    public void test1() {
        Flux.just("1","2","3").subscribe(System.out::println);
    }
}
