package cn.tendata.location.service;

import cn.tendata.location.data.elasticsearch.rest.model.AbstractElasticsearchEntity;

import java.io.Serializable;

public interface EntityService<T extends AbstractElasticsearchEntity, ID extends Serializable> {
}
