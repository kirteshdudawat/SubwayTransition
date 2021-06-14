package com.kirtesh.subwaytransition.services.business;

import com.kirtesh.subwaytransition.exception.ApiServiceException;
import com.kirtesh.subwaytransition.response.SearchAvailablePathResponse;

import java.time.LocalDateTime;

public interface PathSearchService {
    public SearchAvailablePathResponse searchAvailablePaths(String fromStation, String toStation, LocalDateTime travelTime) throws ApiServiceException;
}
