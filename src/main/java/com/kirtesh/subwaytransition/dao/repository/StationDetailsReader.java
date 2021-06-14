package com.kirtesh.subwaytransition.dao.repository;

import com.kirtesh.subwaytransition.dao.models.StationDetails;
import com.kirtesh.subwaytransition.exception.ApiServiceException;

import java.util.List;

public interface StationDetailsReader {

    public List<StationDetails> getAllStations() throws ApiServiceException;
}
