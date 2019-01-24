package cn.tendata.location.data.elasticsearch;

import cn.tendata.location.data.elasticsearch.model.IpLocationItem;
import cn.tendata.location.data.elasticsearch.rest.repository.RestClientRepository;

import java.io.IOException;
import java.util.Collection;

public interface IpLocationRepository extends RestClientRepository<IpLocationItem, String> {
    IpLocationItem findByIp(String ip) throws IOException;
    void deleteByStartIpAndEndIp(String startIp, String endIp) throws IOException;
    Collection<IpLocationItem> findByStartIpAndEndIp(String startIp, String endIp) throws IOException;

}
