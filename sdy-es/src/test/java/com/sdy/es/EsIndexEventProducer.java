package com.sdy.es;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import java.util.List;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/2.
 */
public class EsIndexEventProducer {
    private final RingBuffer<EsIndexEvent> ringBuffer;

    public EsIndexEventProducer(RingBuffer<EsIndexEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorOneArg<EsIndexEvent, List<IndexQuery>> TRANSLATOR = (event, sequence, bb) -> {
        event.setIndexQueries(bb);
        event.setSize(bb.size());
    };

    public void onData(List<IndexQuery> indexQueries) {
        ringBuffer.publishEvent(TRANSLATOR, indexQueries);
    }
}
