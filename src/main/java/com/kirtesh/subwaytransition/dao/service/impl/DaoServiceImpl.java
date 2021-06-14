package com.kirtesh.subwaytransition.dao.service.impl;

import com.kirtesh.subwaytransition.dao.models.StationDetails;
import com.kirtesh.subwaytransition.dao.service.DaoService;
import com.kirtesh.subwaytransition.dto.StationDetailsDto;
import com.kirtesh.subwaytransition.enums.DataSource;
import com.kirtesh.subwaytransition.exception.ApiServiceException;
import com.kirtesh.subwaytransition.utils.converter.EntityModelToDtoConverter;
import com.kirtesh.subwaytransition.utils.factory.DataSourceFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("daoService")
public class DaoServiceImpl implements DaoService {

    @Override
    public List<StationDetailsDto> getStationList(DataSource dataSource) throws ApiServiceException {
        List<StationDetails> stationDetailsList = DataSourceFactory.getImplementation(dataSource).getAllStations();
        return EntityModelToDtoConverter.convert(stationDetailsList);
    }
}
