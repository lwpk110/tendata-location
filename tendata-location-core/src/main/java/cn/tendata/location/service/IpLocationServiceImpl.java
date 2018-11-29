package cn.tendata.location.service;

import cn.tendata.location.data.elasticsearch.rest.model.IpLocationItem;
import cn.tendata.location.data.elasticsearch.rest.repository.IpLocationRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class IpLocationServiceImpl extends EntityServiceSupport<IpLocationItem, String, IpLocationRepository>
        implements IpLocationService {

    protected IpLocationServiceImpl(IpLocationRepository repository) {
        super(repository);
    }

    @Override
    public IpLocationItem search(String ip) throws IOException {
        return getRepository().findByIp(ip);
    }

    @Override
    public void delete(String startIp, String endIp) throws IOException {
        getRepository().deleteByStartIpAndEndIp(startIp, endIp);
    }
}
