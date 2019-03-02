package com.sdy.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/2.
 */
@Configuration
@SpringBootApplication(exclude = {WebMvcAutoConfiguration.class})
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
