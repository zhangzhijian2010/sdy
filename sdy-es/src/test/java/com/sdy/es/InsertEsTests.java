package com.sdy.es;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
@Log4j2
public class InsertEsTests {
    @Autowired
    private SysConfig sysConfig;
    @Autowired
    private EsIndexEventHandler esIndexEventHandler;

    private Disruptor<EsIndexEvent> disruptor;

    @Before
    public void init() {
        EventFactory<EsIndexEvent> eventFactory = new IndexEventFactory(sysConfig.getIndexName(), 1000);
        ThreadPoolExecutorFactoryBean threadPoolFactory = new ThreadPoolExecutorFactoryBean();
        threadPoolFactory.setCorePoolSize(sysConfig.getCoreSize());
        threadPoolFactory.setMaxPoolSize(sysConfig.getCoreSize());
        int ringBufferSize = 1024 * 1024; // RingBuffer 大小，必须是 2 的 N 次方；
        Disruptor<EsIndexEvent> disruptor = new Disruptor<>(eventFactory,
                ringBufferSize, threadPoolFactory, ProducerType.SINGLE,
                new YieldingWaitStrategy());
        disruptor.handleEventsWith(esIndexEventHandler);
        disruptor.start();
        this.disruptor = disruptor;
    }


    @Test
    public void startProducer() throws InterruptedException {
        EsIndexEventProducer producer = new EsIndexEventProducer(disruptor.getRingBuffer());
        EventFactory<EsIndexEvent> eventFactory = new IndexEventFactory(sysConfig.getIndexName(), 1000);
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                IntStream.range(0, sysConfig.getProducePerSecond()).forEach(i -> {
                    producer.onData(eventFactory.newInstance().getIndexQueries());
                });
            }
        }, 10l, 990l);
        TimeUnit.HOURS.sleep(2l);
    }


}
