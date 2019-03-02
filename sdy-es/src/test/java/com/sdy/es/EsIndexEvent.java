package com.sdy.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import java.util.List;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/2.
 */
@Data
@AllArgsConstructor
public class EsIndexEvent {

    private List<IndexQuery> indexQueries;
}
