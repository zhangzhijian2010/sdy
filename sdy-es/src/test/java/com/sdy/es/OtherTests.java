package com.sdy.es;

import org.junit.Test;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/2.
 */
public class OtherTests {

    @Test
    public void test() {
        EsIndexEvent esIndexEvent = new IndexEventFactory("XXX", 1000).newInstance();
        System.out.println(esIndexEvent.getIndexQueries().size());
    }
}
