package com.example.webflux.router;

import com.example.webflux.handler.CityHandler;
import com.example.webflux.handler.TimerHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/26.
 */
@Configuration
public class CityRouter {
    @Bean
    public RouterFunction<ServerResponse> routeCity(CityHandler cityHandler, TimerHandler timerHandler) {
        return route(GET("/time"), req -> timerHandler.getTime(req))
                .andRoute(GET("/hello"), req -> cityHandler.helloCity(req))
                .andRoute(GET("/date"), req -> timerHandler.getDate(req))
                .andRoute(GET("/times"), req -> timerHandler.sendTimePerSec(req));
    }

}
