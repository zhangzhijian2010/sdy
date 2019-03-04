package com.sdy.es;

import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/2.
 */
public class OtherTests {

    @Test
    public void test() {
        EsIndexEvent esIndexEvent = new IndexEventFactory("XXX", 1000).newInstance();
        System.out.println(esIndexEvent.getIndexQueries().size());
    }

    @Test
    public void testCreateThread() {

        ThreadPoolExecutorFactoryBean threadPoolFactory = new ThreadPoolExecutorFactoryBean();
        threadPoolFactory.setCorePoolSize(4);
        threadPoolFactory.setMaxPoolSize(4);
        Thread a = threadPoolFactory.createThread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread b = threadPoolFactory.createThread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(a.getId());
        System.out.println(b.getId());

    }
}
