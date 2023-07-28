package com.atlassian.openfeign1474;

import feign.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@EnableFeignClients
@SpringBootApplication
public class Openfeign1474Application {

    public static void main(String[] args) {
        SpringApplication.run(Openfeign1474Application.class, args);
    }
}

@RestController
class ARestController {

    private final PieDevClient pieDevClient;

    ARestController(PieDevClient pieDevClient) {
        this.pieDevClient = pieDevClient;
    }

    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    public void get() {
        var response = pieDevClient.getTransferEncoding("chunked");
    }
}

@FeignClient(value = "piedev")
interface PieDevClient {

    @PostMapping("/response-headers")
    Response getTransferEncoding(@RequestParam(HttpHeaders.TRANSFER_ENCODING) String value);
}