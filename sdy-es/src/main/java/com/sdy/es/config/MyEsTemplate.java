package com.sdy.es.config;

import com.google.common.collect.Lists;
import com.yg.sfa.common.tuple.Tuple;
import com.yg.sfa.common.tuple.Tuple4;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.springframework.data.elasticsearch.ElasticsearchException;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.elasticsearch.index.VersionType.EXTERNAL;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2018/11/7.
 */
@Slf4j
public class MyEsTemplate {

    private Client client;
    private ElasticsearchConverter elasticsearchConverter;
    private ResultsMapper resultsMapper;

    public MyEsTemplate(Client client, ElasticsearchConverter elasticsearchConverter) {
        this.client = client;
        this.elasticsearchConverter = elasticsearchConverter;
        this.resultsMapper = new DefaultResultMapper(elasticsearchConverter.getMappingContext());
    }

    public void bulkIndex(List<IndexQuery> queries, boolean hasId) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (IndexQuery query : queries) {
            bulkRequest.add(prepareIndex(query));
        }
        checkForBulkUpdateFailure(bulkRequest.execute().actionGet(), hasId);
    }

    private void checkForBulkUpdateFailure(BulkResponse bulkResponse, boolean hasId) {
        Map<String, String> failedDocuments = new HashMap<>();
        boolean failed = false;
        for (int i = 0, len = bulkResponse.getItems().length; i < len; i++) {
            BulkItemResponse item = bulkResponse.getItems()[i];
            if (item.isFailed()) {
                failed = true;
                if (hasId) {
                    failedDocuments.put(item.getId(), item.getFailureMessage());
                } else {
                    failedDocuments.put(String.valueOf(i), item.getFailureMessage());
                }
            }
        }
        if (failed) {
            throw new ElasticsearchException(
                    "Bulk indexing has failures. Use ElasticsearchException.getFailedDocuments() for detailed messages ["
                            + failedDocuments + "]",
                    failedDocuments);
        }
    }

    private IndexRequestBuilder prepareIndex(IndexQuery query) {
        try {
            String indexName = StringUtils.isEmpty(query.getIndexName())
                    ? retrieveIndexNameFromPersistentEntity(query.getObject().getClass())[0]
                    : query.getIndexName();
            String type = StringUtils.isEmpty(query.getType()) ? retrieveTypeFromPersistentEntity(query.getObject().getClass())[0]
                    : query.getType();

            IndexRequestBuilder indexRequestBuilder = null;

            if (query.getObject() != null) {
                String id = StringUtils.isEmpty(query.getId()) ? getPersistentEntityId(query.getObject()) : query.getId();
                // If we have a query id and a document id, do not ask ES to generate one.
                if (id != null) {
                    indexRequestBuilder = client.prepareIndex(indexName, type, id);
                } else {
                    indexRequestBuilder = client.prepareIndex(indexName, type);
                }
                indexRequestBuilder.setSource(resultsMapper.getEntityMapper().mapToString(query.getObject()),
                        Requests.INDEX_CONTENT_TYPE);
            } else if (query.getSource() != null) {
                indexRequestBuilder = client.prepareIndex(indexName, type, query.getId()).setSource(query.getSource(),
                        Requests.INDEX_CONTENT_TYPE);
            } else {
                throw new ElasticsearchException(
                        "object or source is null, failed to index the document [id: " + query.getId() + "]");
            }
            if (query.getVersion() != null) {
                indexRequestBuilder.setVersion(query.getVersion());
                indexRequestBuilder.setVersionType(EXTERNAL);
            }

            if (query.getParentId() != null) {
                indexRequestBuilder.setParent(query.getParentId());
            }
            return indexRequestBuilder;
        } catch (IOException e) {
            throw new ElasticsearchException("failed to index the document [id: " + query.getId() + "]", e);
        }
    }


    private String[] retrieveIndexNameFromPersistentEntity(Class clazz) {
        if (clazz != null) {
            return new String[]{getPersistentEntityFor(clazz).getIndexName()};
        }
        return null;
    }

    private String[] retrieveTypeFromPersistentEntity(Class clazz) {
        if (clazz != null) {
            return new String[]{getPersistentEntityFor(clazz).getIndexType()};
        }
        return null;
    }

    private String getPersistentEntityId(Object entity) {

        ElasticsearchPersistentEntity<?> persistentEntity = getPersistentEntityFor(entity.getClass());
        Object identifier = persistentEntity.getIdentifierAccessor(entity).getIdentifier();

        if (identifier != null) {
            return identifier.toString();
        }

        return null;
    }

    public ElasticsearchPersistentEntity getPersistentEntityFor(Class clazz) {
        Assert.isTrue(clazz.isAnnotationPresent(Document.class), "Unable to identify index name. " + clazz.getSimpleName()
                + " is not a Document. Make sure the document class is annotated with @Document(indexName=\"foo\")");
        return elasticsearchConverter.getMappingContext().getRequiredPersistentEntity(clazz);
    }


    /**
     * <p>
     * 几个问题:
     * 1. es-max-write-queues 目前设置10 (es 可存的最大队列是200)，可以大部分减少 EsRejectedExecutionException， 但仍然会有重复 ?
     * 2. EsRejectedExecutionException 后，重复存入，有可能造成数据重复插入 ?
     * </p>
     *
     * @param queries
     * @param hasId 提交的index 是指定的id 还是 es 自动生成的id
     * @return
     */
    public boolean bulkIndexAndRetryCommitRejects(List<IndexQuery> queries, boolean hasId) {
        LongAdder rejectTimes = new LongAdder();
        while (true) {
            try {
                bulkIndex(queries, hasId);
                return true;
            } catch (Exception e) {
                if (e instanceof ElasticsearchException) {
                    Map<String, String> faildDocuments = ((ElasticsearchException) e).getFailedDocuments();
                    Tuple4<Integer, Integer, Integer, List<IndexQuery>> failIndexs = anaFailIndexs(queries, hasId, faildDocuments);
                    rejectTimes.increment();
                    if (failIndexs.e2 > 0) {
                        log.error("EsRejectedExecutionException, 异常: 拒绝次数: {}, 批量总条数: {}, 成功条数: {}, Reject 条数: {}, 其它异常条数: {} ", rejectTimes.intValue(), failIndexs.e1, failIndexs.e1 - faildDocuments.size(),
                                failIndexs.e2, failIndexs.e3);
                        queries = failIndexs.e4;
                    } else {
                        log.error("ElasticsearchException 但非 EsRejectedExecutionException 异常:{}, {}", e.getClass().getName(), ExceptionUtils.getStackTrace(e));
                        return false;
                    }
                } else {
                    log.error("批量插入es异常:{}, {}", e.getClass().getName(), ExceptionUtils.getStackTrace(e));
                    return false;
                }
            }
        }
    }

    /**
     * 解析提交到es失败的数据
     *
     * @param queries 当前要插入的索引列表
     * @param hasId 这批索引中的id是否业务生成. true: 业务生成的. false: es 自动生成 效率很高 .
     * @param faildDocuments 失败的文档
     * @return <p> Tuple4<总数, 拒绝的条数, 其它异常数据, List<IndexQuery> 被决绝待重新提交的数据>  </p>
     */
    private Tuple4<Integer, Integer, Integer, List<IndexQuery>> anaFailIndexs(List<IndexQuery> queries, boolean hasId, Map<String, String> faildDocuments) {
        int total = queries.size();
        int rejectSize = 0;
        LongAdder otherFailSize = new LongAdder();
        List<String> rejectIndexIds = new ArrayList<>();

        faildDocuments.forEach((k, v) -> {
            if (v != null && v.contains("EsRejectedExecutionException")) {
                rejectIndexIds.add(k);
            } else {
                otherFailSize.increment();
            }
        });
        rejectSize = rejectIndexIds.size();
        // 没有拒绝的条数，直接返回
        if (rejectSize == 0) {
            return Tuple.of(total, rejectSize, otherFailSize.intValue(), Lists.newArrayList());
        }
        List<IndexQuery> rejectIndexQuerys = Lists.newArrayList();
        if (hasId) {
            Map<String, IndexQuery> id2IndexMap = queries.stream().collect(Collectors.toMap(IndexQuery::getId, Function.identity()));
            for (String id : rejectIndexIds) {
                rejectIndexQuerys.add(id2IndexMap.get(id));
            }
        } else {
            for (String indexStr : rejectIndexIds) {
                rejectIndexQuerys.add(queries.get(Integer.parseInt(indexStr)));
            }
        }
        return Tuple.of(total, rejectSize, otherFailSize.intValue(), rejectIndexQuerys);
    }
}


