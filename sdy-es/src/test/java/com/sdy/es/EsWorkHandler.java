package com.sdy.es;

import com.lmax.disruptor.WorkHandler;
import com.sdy.es.config.MyEsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/2.
 */
@Component
@Scope("prototype")
@Slf4j
public class EsWorkHandler implements WorkHandler<EsIndexEvent> {

    @Autowired
    private MyEsTemplate myEsTemplate;

    @Override
    public void onEvent(EsIndexEvent event) {
        long st = System.currentTimeMillis();
        if (event.getSize() > 0) {
            myEsTemplate.bulkIndexAndRetryCommitRejects(event.getIndexQueries(), false);
        }
        log.info("event: {}, cost: {} ms", event.getSize(), System.currentTimeMillis() - st);
    }
}
