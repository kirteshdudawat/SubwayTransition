package com.kirtesh.subwaytransition.services.validation;

import com.kirtesh.subwaytransition.exception.ApiServiceException;
import com.kirtesh.subwaytransition.sro.SearchAvailablePathRequestSro;

public interface ValidationService {
    public void validateRequest(SearchAvailablePathRequestSro requestSro) throws ApiServiceException;
}
