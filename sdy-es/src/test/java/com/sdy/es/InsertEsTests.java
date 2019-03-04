package com.sdy.es;

import com.google.common.collect.Lists;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.sdy.es.config.ApplicationService;
import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
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


    private Disruptor<EsIndexEvent> disruptor;

    @Autowired
    private ApplicationService applicationService;

    @Before
    public void init() {
        log.info("sysConfig: {}", sysConfig.toString());
        EventFactory<EsIndexEvent> eventFactory = new IndexEventFactory(sysConfig.getIndexName(), 1000);
        ThreadPoolExecutorFactoryBean threadPoolFactory = new ThreadPoolExecutorFactoryBean();
        threadPoolFactory.setThreadNamePrefix("xxxx-");
        threadPoolFactory.getObject();
        threadPoolFactory.setCorePoolSize(sysConfig.getCoreSize());
        threadPoolFactory.setMaxPoolSize(sysConfig.getCoreSize());
        int ringBufferSize = 4 * 1024; // RingBuffer 大小，必须是 2 的 N 次方；
        Disruptor<EsIndexEvent> disruptor = new Disruptor<>(eventFactory,
                ringBufferSize, threadPoolFactory, ProducerType.MULTI,
                new YieldingWaitStrategy());

        List<EsWorkHandler> esWorkHandlers = Lists.newArrayList();
        IntStream.range(0, sysConfig.getCoreSize()).forEach(i -> {
            esWorkHandlers.add(applicationService.getBean(EsWorkHandler.class));
        });
        disruptor.handleEventsWithWorkerPool(esWorkHandlers.toArray(new EsWorkHandler[]{}));
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
                long st = System.currentTimeMillis();
                IntStream.range(0, sysConfig.getProducePerSecond()).forEach(i -> {
                    producer.onData(((IndexEventFactory) eventFactory).newInstance2().getIndexQueries());
                });
                log.info("create {}, cost: {}", sysConfig.getProducePerSecond() * 1000, System.currentTimeMillis() - st);
            }
        }, 10l, 500l);
        TimeUnit.HOURS.sleep(2l);
    }


}
