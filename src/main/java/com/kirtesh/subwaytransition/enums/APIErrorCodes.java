package com.kirtesh.subwaytransition.enums;

import java.util.HashMap;
import java.util.Map;

/*
 * author : kirtesh
 */
public enum APIErrorCodes {
	INVALID(1001,"Some unknown error occuured"),
	INVALID_COMPARISON_OF_OBJECT(1002,"Invalid comparison of objects."),

	CACHE_EXCEPTION(1201,"Exception Occurred while fetching Data from Cache"),
	NO_DATA_PRESENT(1202,"No Data related to request is present in Cache"),

	INVALID_REQUEST_PARAMETER(1301,"Requested source or destination does not exist."),
	INVALID_DATE_PARAMETER(1302,"Requested time of travel is in past, please provide correct travel time."),
	SAME_SOURCE_AND_DESTINATION(1303,"Requested travel between source and destination station are same (Please provide different source and destination station)."),

	CONVERTER_SERVICE_EXCEPTION(1401,"Unable to Convert Request to desired format"),

	UNABLE_TO_READ_DATA_FROM_DATA_SOURCE(1501, "Unable to read data from data source"),
	INVALID_DATA_AT_DATA_SOURCE(1502, "Invalid data present at data source"),
	REQUESTED_DATA_SOURCE_NOT_AVAILABLE(1503, "Requested data source is not available"),
	MISSING_LINE_CODE_CONFIG(1504, "Line Code Configuration is missing"),
	UNKNOWN_TIME_TYPE_CODE(1505, "Unknown timeType configuration found in application.yml."),

	NO_PATH_AVAILABLE_BETWEEN_SOURCE_AND_DESTINATION(1601, "No way exist between selected source and destination. It could be due to station / lines are non-operational for selected time.");
		
	private static Map<Integer, APIErrorCodes> codeToValueMap = new HashMap<Integer, APIErrorCodes>();
	private Integer code;
	private String message;
		
	private APIErrorCodes(int code, String message){
		this.code = code;
		this.message = message;
	}

	public Integer code() {
		return null == code ? INVALID.code() : code;
	}

	public String message() {
		return message;
	}

    static {
        for (APIErrorCodes apiErrorCode : APIErrorCodes.values()) {
            codeToValueMap.put(apiErrorCode.code, apiErrorCode);
        }
    }
    
    public static APIErrorCodes getValueof(Integer code) {
        if (codeToValueMap.containsKey(code))
            return codeToValueMap.get(code);
        return APIErrorCodes.INVALID;
    }
}

