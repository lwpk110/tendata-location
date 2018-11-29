package cn.tendata.location.service;

import cn.tendata.location.data.elasticsearch.rest.model.IpLocationItem;

import java.io.IOException;

public interface IpLocationService extends EntityService<IpLocationItem, String> {
    IpLocationItem search(String ip) throws IOException;
}
