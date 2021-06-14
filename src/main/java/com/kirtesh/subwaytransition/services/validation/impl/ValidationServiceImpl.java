package com.kirtesh.subwaytransition.services.validation.impl;

import com.kirtesh.subwaytransition.cache.JvmCache;
import com.kirtesh.subwaytransition.enums.APIErrorCodes;
import com.kirtesh.subwaytransition.exception.ApiServiceException;
import com.kirtesh.subwaytransition.services.validation.ValidationService;
import com.kirtesh.subwaytransition.sro.SearchAvailablePathRequestSro;
import com.kirtesh.subwaytransition.utils.validator.DataValidator;
import org.springframework.stereotype.Service;

@Service("validationService")
public class ValidationServiceImpl implements ValidationService {

    /**
     * Validates incoming request.
     *
     * @param requestSro -> incoming request.
     * @throws ApiServiceException in case of error
     */
    @Override
    public void validateRequest(SearchAvailablePathRequestSro requestSro) throws ApiServiceException {
        if (!JvmCache.getStationCodeToDtoMap().containsKey(requestSro.getSourceStationCode()) ||
                !JvmCache.getStationCodeToDtoMap().containsKey(requestSro.getDestinationStationCode())){
            throw new ApiServiceException(APIErrorCodes.INVALID_REQUEST_PARAMETER);
        }

        if (requestSro.getDateTime() != null && DataValidator.isPastDatetime(requestSro.getDateTime())){
            throw new ApiServiceException(APIErrorCodes.INVALID_DATE_PARAMETER);
        }

        if (requestSro.getDestinationStationCode().equalsIgnoreCase(requestSro.getSourceStationCode())){
            throw new ApiServiceException(APIErrorCodes.SAME_SOURCE_AND_DESTINATION);
        }
    }
}
