package cn.tendata.location.rest.web.controller;

import cn.tendata.location.data.elasticsearch.rest.model.IpLocationItem;
import cn.tendata.location.service.IpLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
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

    @GetMapping
    public ResponseEntity<IpLocationItem> location(@NotBlank(message = "Param.ip.notBlank") @RequestParam String ip)
            throws IOException {
        final IpLocationItem location = ipLocationService.search(ip);
        return ok(location);
    }

    @DeleteMapping
    public ResponseEntity delete(@NotBlank(message = "Param.startIp.notBlank") @RequestParam String startIp,
                                 @NotBlank(message = "Param.endIp.notBlank") @RequestParam String endIp) throws
            IOException {
        ipLocationService.delete(startIp, endIp);
        return ok().build();
    }
}
