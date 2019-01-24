package cn.tendata.location.data.elasticsearch.rest.repository;

import cn.tendata.location.data.elasticsearch.model.AbstractElasticsearchEntity;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchRequest;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

public interface RestClientRepository<T extends AbstractElasticsearchEntity, ID extends Serializable> {
    <S extends T> void bulkIndex(Iterable<S> items);
    <S extends T> S search(SearchRequest request) throws IOException;
    <S extends T> Collection<S> searchCollection(SearchRequest request) throws IOException;
    void delete(DeleteRequest deleteRequest) throws IOException;
    void deleteById(ID id) throws IOException;

}

