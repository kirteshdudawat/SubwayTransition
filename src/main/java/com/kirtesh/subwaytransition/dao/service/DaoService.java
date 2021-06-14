package com.kirtesh.subwaytransition.dao.service;

import com.kirtesh.subwaytransition.dto.StationDetailsDto;
import com.kirtesh.subwaytransition.enums.DataSource;
import com.kirtesh.subwaytransition.exception.ApiServiceException;

import java.util.List;

public interface DaoService {

    public List<StationDetailsDto> getStationList(DataSource dataSource) throws ApiServiceException;
}
