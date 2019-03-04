package com.sdy.es;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.LongAdder;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/4.
 */
public class Counter {

    private final static LongAdder count = new LongAdder();

    /**
     * 新增数据
     *
     * @param size
     */
    public static void increase(long size) {
        count.add(size);
    }

    /**
     * 得到当前总数后，将数据减掉得到的总数(再次初始)
     *
     * @return
     */
    public static long getAndDelGetCount() {
        long v = count.longValue();
        count.add(-v);
        return v;
    }
}
