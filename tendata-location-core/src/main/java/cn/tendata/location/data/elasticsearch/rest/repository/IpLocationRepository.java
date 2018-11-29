package cn.tendata.location.data.elasticsearch.rest.repository;

import cn.tendata.location.data.elasticsearch.rest.model.IpLocationItem;

import java.io.IOException;

public interface IpLocationRepository extends RestClientRepository<IpLocationItem, String>  {
    IpLocationItem findByIp(String ip) throws IOException;
    void deleteByStartIpAndEndIp(String startIp, String endIp) throws IOException;
}
