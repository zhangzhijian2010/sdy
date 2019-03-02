package com.sdy.es;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
@Log4j2
public class CreateEsIndexTests {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private SysConfig sysConfig;

    public boolean create(String indexName, String typeName) {
        if (!elasticsearchTemplate.indexExists(indexName)) {
            try {
                XContentBuilder builder = XContentFactory.jsonBuilder()
                        .startObject()
                        .startObject("properties")
                        .startObject("did").field("type", "keyword").endObject()
                        .startObject("content").field("type", "text").field("index", "false").endObject()
                        .startObject("did2").field("type", "keyword").endObject()
                        .startObject("did3").field("type", "keyword").endObject()
                        .endObject()
                        .endObject();
                XContentBuilder settings = XContentFactory.jsonBuilder().startObject().startObject("index").field("refresh_interval", "30s")
                        .field("max_result_window", 100000).field("number_of_replicas", "0").field("number_of_shards", "4")
                        .startObject("translog")
                        .field("durability", "async")
                        .field("sync_interval", "30s")
                        .endObject()
                        .startObject("merge").startObject("scheduler").field("max_thread_count", "4").
                                endObject().
                                endObject()
                        .endObject().endObject();

                elasticsearchTemplate.createIndex(indexName, settings);
                elasticsearchTemplate.putMapping(indexName, typeName, builder);
                log.info("create index : " + indexName + "," + typeName);
                return true;
            } catch (Exception e) {
                log.error("创建索引:{},{} 异常: {}", indexName, typeName, ExceptionUtils.getStackTrace(e));
            }
            return false;
        }
        return true;
    }

    @Test
    public void testCreateIndex() {
        create(sysConfig.getIndexName(), sysConfig.getIndexName());
    }
}
