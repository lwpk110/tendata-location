package cn.tendata.location.rest.web.controller;

import cn.tendata.location.core.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.tendata.location.core.Response.success;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/ip_location")
public class IpLocationController {

    @GetMapping(value = {"","/"})
    public ResponseEntity<Response> location(@RequestParam @Validated String ip){
        return ok(success());
    }
}
