package com.sdy.es;

import com.lmax.disruptor.EventHandler;
import com.sdy.es.config.MyEsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/2.
 */
@Component
@Slf4j
public class EsIndexEventHandler implements EventHandler<EsIndexEvent> {

    @Autowired
    private MyEsTemplate myEsTemplate;

    @Override
    public void onEvent(EsIndexEvent event, long sequence, boolean endOfBatch) throws Exception {
        long st = System.currentTimeMillis();
        myEsTemplate.bulkIndexAndRetryCommitRejects(event.getIndexQueries(), false);
        log.info("event: {}, cost: {} ms", 1000, System.currentTimeMillis() - st);
    }
}
