package com.kirtesh.subwaytransition.utils.converter;

import com.kirtesh.subwaytransition.cache.models.LineConfigs;
import com.kirtesh.subwaytransition.config.models.TimeBasedLineConfig;
import com.kirtesh.subwaytransition.dao.models.StationDetails;
import com.kirtesh.subwaytransition.dto.StationDetailsDto;
import com.kirtesh.subwaytransition.enums.APIErrorCodes;
import com.kirtesh.subwaytransition.exception.ApiServiceException;
import com.kirtesh.subwaytransition.utils.DateTimeUtility;
import com.kirtesh.subwaytransition.utils.validator.DataValidator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Converters to convert Entities / Models to DTO.
 * Entities are generally used to interact with DataSource.
 * DTO -> Data Transport Objects.
 */
@Component
public class EntityModelToDtoConverter {
    public static List<StationDetailsDto> convert(List<StationDetails> stationDetailsList) throws ApiServiceException {
        List<StationDetailsDto> stationDetailsDtos = new ArrayList<>();
        for(StationDetails stationDetails: stationDetailsList) {
            StationDetailsDto stationDetailsDto = convert(stationDetails);
            stationDetailsDtos.add(stationDetailsDto);
        }
        return stationDetailsDtos;
    }

    public static LineConfigs convert(TimeBasedLineConfig timeBasedLineConfig) {
        LineConfigs lineConfigs = new LineConfigs();
        lineConfigs.setExpression(timeBasedLineConfig.getExpression());
        lineConfigs.setExchangeWaitTime(timeBasedLineConfig.getExchangeWaitTime());
        lineConfigs.setTravelTimeBetweenStation(timeBasedLineConfig.getTravelTimeBetweenStation());

        return lineConfigs;
    }

    private static StationDetailsDto convert(StationDetails stationDetails) throws ApiServiceException {

        if(DataValidator.validateStationCode(stationDetails.getStationCode())) {
            String lineCode = stationDetails.getStationCode().substring(0,2);
            String stationNumber = stationDetails.getStationCode().substring(2);
            LocalDate openingDate = DateTimeUtility.convertStringToLocalDate(stationDetails.getOpeningDate());

            StationDetailsDto stationDetailsDto = new StationDetailsDto();
            stationDetailsDto.setStationCode(stationDetails.getStationCode());
            stationDetailsDto.setLineCode(lineCode);
            stationDetailsDto.setStationNumber(Integer.parseInt(stationNumber));
            stationDetailsDto.setStationName(stationDetails.getStationName());
            stationDetailsDto.setOpeningDate(openingDate);

            return stationDetailsDto;
        }
        throw new ApiServiceException(APIErrorCodes.INVALID_DATA_AT_DATA_SOURCE);
    }
}
