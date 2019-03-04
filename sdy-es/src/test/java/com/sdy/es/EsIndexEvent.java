package com.sdy.es;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import java.util.List;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/2.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsIndexEvent {

    private List<IndexQuery> indexQueries = Lists.newArrayList();

    private int size = 0;

}
