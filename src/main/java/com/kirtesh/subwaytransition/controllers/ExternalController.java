package com.kirtesh.subwaytransition.controllers;

import com.kirtesh.subwaytransition.exception.ApiServiceException;
import com.kirtesh.subwaytransition.response.Response;
import com.kirtesh.subwaytransition.response.SearchAvailablePathResponse;
import com.kirtesh.subwaytransition.services.business.PathSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class ExternalController {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalController.class);

    @Autowired
    private PathSearchService pathSearchService;

    /**
     *
     * @param fromStation - Station Code eg. EW20
     * @param toStation - same as fromStation
     * @param travelTime - Time in format 2019-01-01T16:00 (yyyy-MM-ddTHH:mm)
     *
     * @return Available path from source to destination
     */
    @GetMapping(value = "/search/path/{fromStation}/{toStation}")
    public ResponseEntity<Response<SearchAvailablePathResponse>> getPaths(
            @PathVariable("fromStation") String fromStation,
            @PathVariable("toStation") String toStation,
            @RequestParam(name="travelTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime travelTime) {

        StopWatch watch = new StopWatch();
        watch.start();

        LOG.info("getPath request received. FromStation: {}, ToStation: {}, TravelTime: {}", fromStation, toStation, travelTime);
        try {
            SearchAvailablePathResponse response = pathSearchService.searchAvailablePaths(fromStation, toStation, travelTime);
            watch.stop();
            LOG.info("getPath returning response: {} in {} milliseconds.", response, watch.getTotalTimeMillis());
            return new ResponseEntity<>(Response.successResponse(response), HttpStatus.OK);
        } catch (ApiServiceException ex) {
            watch.stop();
            LOG.error("getPath returned exception for request: FromStation: {}, ToStation: {}, TravelTime: {} in {} milliseconds. Exception {}", fromStation, toStation, travelTime, watch.getTotalTimeMillis(), ex);
            return new ResponseEntity<>(Response.failureResponseWithErrorCodeAndMessage(ex.getCode().code().toString(), ex.getMessage(), ex.getCode().name()), HttpStatus.EXPECTATION_FAILED);
        } catch (Exception e) {
            watch.stop();
            LOG.error("getPath returned unhandled exception for request: FromStation: {}, ToStation: {}, TravelTime: {} in {} milliseconds. Exception {}", fromStation, toStation, travelTime, watch.getTotalTimeMillis(), e);
            return new ResponseEntity<>(Response.internalServerError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
