package com.kirtesh.subwaytransition.dao.repository.impl;

import com.kirtesh.subwaytransition.dao.models.StationDetails;
import com.kirtesh.subwaytransition.dao.repository.StationDetailsReader;
import com.kirtesh.subwaytransition.enums.APIErrorCodes;
import com.kirtesh.subwaytransition.exception.ApiServiceException;
import com.kirtesh.subwaytransition.utils.csv.CSVReaderUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("csvDataReader")
public class CSVDataReader implements StationDetailsReader {

    private static final Logger LOG = LoggerFactory.getLogger(CSVDataReader.class);

    /**
     * @return all station details.
     * @throws ApiServiceException if data in csv is not valid.
     */
    @Override
    public List<StationDetails> getAllStations() throws ApiServiceException {
        try {
            return CSVReaderUtility.readCsvFile("StationMap.csv", StationDetails.class);
        } catch (Exception e) {
            LOG.error("Unable to read data from CSV. Failed at CSVDataReader.getAllStations {}", e);
            throw new ApiServiceException(APIErrorCodes.UNABLE_TO_READ_DATA_FROM_DATA_SOURCE);
        }
    }
}
