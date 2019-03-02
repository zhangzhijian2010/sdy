package com.sdy.es;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/2.
 */
@Component
@ConfigurationProperties("config")
@Data
public class SysConfig {

    private int producePerSecond;

    private String indexName;

    private int coreSize;

    
}
