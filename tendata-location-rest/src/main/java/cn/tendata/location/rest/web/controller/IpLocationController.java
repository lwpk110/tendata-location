package cn.tendata.location.rest.web.controller;

import cn.tendata.location.data.elasticsearch.rest.model.IpLocationItem;
import cn.tendata.location.service.IpLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.IOException;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/ip_location")
@SuppressWarnings("unused")
@Validated
public class IpLocationController {

    private final IpLocationService ipLocationService;

    @Autowired
    public IpLocationController(IpLocationService ipLocationService) {
        this.ipLocationService = ipLocationService;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<IpLocationItem> location(@NotNull(message = "Param.ip.notBlank") @RequestParam String ip)
            throws IOException {
        final IpLocationItem location = ipLocationService.search(ip);
        return ok(location);
    }
}
