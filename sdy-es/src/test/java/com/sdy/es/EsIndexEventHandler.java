package com.sdy.es;

import com.lmax.disruptor.EventHandler;
import com.sdy.es.config.MyEsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/2.
 */
@Component
public class EsIndexEventHandler implements EventHandler<EsIndexEvent> {

    @Autowired
    private MyEsTemplate myEsTemplate;

    @Override
    public void onEvent(EsIndexEvent event, long sequence, boolean endOfBatch) throws Exception {
        myEsTemplate.bulkIndexAndRetryCommitRejects(event.getIndexQueries(), false);
    }
}
