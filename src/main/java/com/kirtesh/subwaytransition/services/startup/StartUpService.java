package com.kirtesh.subwaytransition.services.startup;

import com.kirtesh.subwaytransition.exception.ApiServiceException;

/*
 * author : kirtesh
 */
public interface StartUpService {
	void executeStartupTasks() throws ApiServiceException;
}
