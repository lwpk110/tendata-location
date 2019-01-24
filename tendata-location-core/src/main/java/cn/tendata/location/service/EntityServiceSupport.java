package cn.tendata.location.service;

import cn.tendata.location.data.elasticsearch.model.AbstractElasticsearchEntity;
import cn.tendata.location.data.elasticsearch.rest.repository.RestClientRepository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

public abstract class EntityServiceSupport<T extends AbstractElasticsearchEntity, ID extends Serializable,
        TRepository extends RestClientRepository<T, ID>> implements EntityService<T,ID> {

    private final TRepository repository;
    private Class<T> entityClass;

    public Class<T> getEntityClass() {
        return entityClass;
    }

    @SuppressWarnings("unchecked")
    protected EntityServiceSupport(TRepository repository) {
        this.repository = repository;
        this.entityClass =(Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected EntityServiceSupport(TRepository repository, Class<T> entityClass) {
        this.repository = repository;
        this.entityClass = entityClass;
    }

    protected TRepository getRepository() {
        return this.repository;
    }




}
