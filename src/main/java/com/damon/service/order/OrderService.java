package com.damon.service.order;

import com.damon.dao.JOrderMapper;
import com.damon.pojo.JOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private JOrderMapper jOrderMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public Map<String, Object> listOrder(Map<String, Object> params) throws  Exception{
        Map<String, Object> result = new HashMap<>();
        long start =  System.currentTimeMillis();
        if (StringUtils.equals((String) params.get("dataSource"), "ES")) {
            final SearchRequest searchRequest = new SearchRequest("j_order");
            searchRequest.types("doc");
            final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            final MultiMatchQueryBuilder multiMatchQueryBuidler = QueryBuilders.multiMatchQuery(params.get("description"));
            multiMatchQueryBuidler.operator(Operator.AND);
            boolQueryBuilder.must(multiMatchQueryBuidler);
            searchSourceBuilder.query(boolQueryBuilder);
            searchSourceBuilder.from(0);
            searchSourceBuilder.size(10000);
            searchSourceBuilder.timeout(new TimeValue(30, TimeUnit.SECONDS));
            searchRequest.source(searchSourceBuilder);
            log.info("searchRequest={}", searchRequest.toString());
            final SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            final RestStatus status = searchResponse.status();
            if (RestStatus.OK == status) {
                final SearchHits hits = searchResponse.getHits();
                final SearchHit[] searchHits = hits.getHits();
                result.put("count",searchHits.length);
            }
            result.put("cost", (System.currentTimeMillis() - start) + "ms");
        } else {
            List<JOrder> list = jOrderMapper.listOrder(params);
            result.put("count", list.size());
            result.put("cost", (System.currentTimeMillis() - start) + "ms");
        }


        return result;
    }

    public JOrder getOrder() {
        return jOrderMapper.selectByPrimaryKey("123");
    }

    public String batchCreateOrder(Long batchCount) {
        List<JOrder> list = new ArrayList<>();
        for (int i=0; i<batchCount; i++) {
            JOrder order = new JOrder();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            order.setId(uuid);
            Integer amtInt = new Random().nextInt(10000);
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(amtInt)).setScale(2);
            order.setAmt(bigDecimal);
            order.setDescription("hi, this is testing for ES by jacob " + bigDecimal);
            order.setOrderNum(String.valueOf(i));
            order.setUpdatedDate(new Date());
            list.add(order);
        }
        jOrderMapper.batchInsert(list);
        return "201";
    }

    public String batchSyncOrderToES() throws Exception{
        final List<JOrder> orders = jOrderMapper.listOrder(new HashMap<>());
        final BulkRequest bulkRequest = new BulkRequest();
        ObjectMapper mapper = new ObjectMapper();
        for (JOrder order : orders) {
            final IndexRequest indexRequest = new IndexRequest("j_order", "doc", order.getId());
            String orderJson = mapper.writeValueAsString(order);
            indexRequest.source(orderJson, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        final BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest);
        for (final BulkItemResponse bulkItemResponse : bulkResponse) {
            if (bulkItemResponse.isFailed()) {
                final BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                final String indexName = failure.getIndex();
                final String indexId = failure.getId();
                final String cause = failure.getMessage();
                log.error("Fail to save index:{}, id:{} to ES server, cause: {}"
                        , indexName, indexId, cause);
            }
        }
        return "201";
    }
}
