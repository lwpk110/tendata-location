package cn.tendata.location.client.rest.repository;

import cn.tendata.location.client.rest.model.AbstractElasticsearchEntity;

import java.io.Serializable;

public interface RestClientRepository<T extends AbstractElasticsearchEntity, ID extends Serializable> {
    <S extends T> void bulkIndex(Iterable<S> items);
}
