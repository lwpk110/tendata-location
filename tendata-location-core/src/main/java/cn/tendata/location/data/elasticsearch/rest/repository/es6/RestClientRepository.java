/*
package cn.tendata.location.data.elasticsearch.rest.repository;

import cn.tendata.location.data.elasticsearch.model.AbstractElasticsearchEntity;
import org.apache.lucene.queries.SearchAfterSortedDocQuery;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;

import java.io.IOException;
import java.io.Serializable;

public interface RestClientRepository<T extends AbstractElasticsearchEntity, ID extends Serializable> {
    <S extends T> void bulkIndex(Iterable<S> items);
    <S extends T> S search(SearchRequest request) throws IOException;
    void delete(DeleteByQueryRequest deleteRequest) throws IOException;
    void refresh() throws IOException;
}
*/
