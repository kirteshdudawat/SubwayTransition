package com.kirtesh.subwaytransition.controllers;

import com.kirtesh.subwaytransition.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckController.class);

    @GetMapping(value = "/healthCheck")
    public ResponseEntity<Response<String>> getBalance(){

        LOG.info("Health Check Request");
        String response = "healthCheck successful.";
        return new ResponseEntity<Response<String>>(Response.successResponse(response), HttpStatus.OK);
    }
}
