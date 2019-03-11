// Copyright (c) 1998-2019 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.5.0.1
// ============================================================================
// CHANGE LOG
// CNT.5.0.1 : 2019-XX-XX, damon.huang, creation
// ============================================================================
package com.damon.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.damon.constansts.ESIndexContext;

/**
 * @author damon.huang
 */
@Component
public class ElasticsearchUtil {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void batchIndex(final List<ESIndexContext> indexContexts) throws IOException {
        if (CollectionUtils.isEmpty(indexContexts)) {
            return;
        }
        final BulkRequest bulkRequest = new BulkRequest();
        @SuppressWarnings("rawtypes")
        final List<DocWriteRequest> indicesRequests = creatIndicesRequest(indexContexts);
        bulkRequest.add(indicesRequests);
        final BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest);
        for (final BulkItemResponse bulkItemResponse : bulkResponse) {
            logIndexInfo(bulkItemResponse.getResponse());
            if (bulkItemResponse.isFailed()) {
                final BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                final String indexName = failure.getIndex();
                final String indexId = failure.getId();
                final String cause = failure.getMessage();
                // log.error("Fail to save index:{}, id:{} to ES server, cause: {}"
                // ,indexName, indexId, cause);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static List<DocWriteRequest> creatIndicesRequest(final List<ESIndexContext> indexContexts) {
        final List<DocWriteRequest> requests = new ArrayList<DocWriteRequest>();
        for (final ESIndexContext indexContext : indexContexts) {
            final DocWriteRequest request = createSingleIndex(indexContext);
            requests.add(request);
        }
        return requests;
    }

    private static void logIndexInfo(final DocWriteResponse indexResponse) {
        final String index = indexResponse.getIndex();
        final String id = indexResponse.getId();
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
            // log.info("finished index, operator=create, index=" + index + ", id=" + id);
        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            // log.info("finished index, operator=update, index=" + index + ", id=" + id);
        } else if (indexResponse.getResult() == DocWriteResponse.Result.DELETED) {
            // log.info("finished index, operator=delete, index=" + index + ", id=" + id);
        }
    }

    @SuppressWarnings("rawtypes")
    public static DocWriteRequest createSingleIndex(final ESIndexContext indexContext) {
        DocWriteRequest request = null;
        final boolean delete = indexContext.isDelete();
        final String entityId = indexContext.getEntityId();
        final String entityName = indexContext.getEntityName();
        // final String indexName = CbxESTCFactory.getIndexName(entityName);
        final String indexName = StringUtils.lowerCase(entityName) + "_damon";
        final String indexType = "doc";
        if (delete) {
            final DeleteRequest deleteRequest = new DeleteRequest(indexName, indexType, entityId);
            request = deleteRequest;
            // log.debug("create delete index requeset, indexName:{}, indexId {}", indexName, entityId);
        } else {
            final IndexRequest indexRequest = new IndexRequest(indexName, indexType, entityId);
            indexRequest.source(indexContext.getJson(), XContentType.JSON);
            request = indexRequest;
            // log.debug("create save index requeset, indexName:{}, indexId {}, json:{}", indexName, entityId,
            // indexContext.getJson());
        }
        return request;
    }

    public String getDoc(String entityName, String id) throws IOException {
        final GetRequest getRequest = new GetRequest(StringUtils.lowerCase(entityName + "_damon"), "doc", id);
        String sourceAsString = StringUtils.EMPTY;
        try {
            final GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            if (getResponse.isExists()) {
                final long version = getResponse.getVersion();
                sourceAsString = getResponse.getSourceAsString();
            }
            return sourceAsString;
        } catch (final ElasticsearchException e) {
//            if (e.status() == RestStatus.NOT_FOUND) {
//
//            }
            throw e;
        }
    }

}
