package com.sdy.es.config;

import org.elasticsearch.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2018/11/7.
 */
@Configuration
public class MyEsTemplateConfig {

    @Bean(name="es.MyEsTemplate")
    public MyEsTemplate myEsTemplate(Client client,
                                                       ElasticsearchConverter converter) {
        try {
            return new MyEsTemplate(client, converter);
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
