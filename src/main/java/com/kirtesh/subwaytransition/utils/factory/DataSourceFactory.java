package com.kirtesh.subwaytransition.utils.factory;

import com.kirtesh.subwaytransition.dao.repository.StationDetailsReader;
import com.kirtesh.subwaytransition.enums.APIErrorCodes;
import com.kirtesh.subwaytransition.enums.DataSource;
import com.kirtesh.subwaytransition.exception.ApiServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DataSourceFactory {

    private static StationDetailsReader csvDataReader;

    /**
     * Factory pattern to change datasource on the fly.
     *
     * @param dataSource - Its an enum to define which data source we need.
     * @return -> implementation of required datasource.
     * @throws ApiServiceException when implementation of data source do not exist.
     */
    public static StationDetailsReader getImplementation(DataSource dataSource) throws ApiServiceException {
        switch (dataSource) {
            case CSV_FILE:
                return csvDataReader;
        }
        throw new ApiServiceException(APIErrorCodes.REQUESTED_DATA_SOURCE_NOT_AVAILABLE);
    }

    @Autowired
    private DataSourceFactory(@Qualifier("csvDataReader") StationDetailsReader csvDataReader) {
        this.csvDataReader = csvDataReader;
    }
}
