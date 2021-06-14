package com.kirtesh.subwaytransition.exception;

import com.kirtesh.subwaytransition.enums.APIErrorCodes;

public class ApiServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	private String message = null;

	private APIErrorCodes code;

	public ApiServiceException(APIErrorCodes code) {
		super(code.message());
		this.code = code;
		this.message = code.message();
	}

	public ApiServiceException(APIErrorCodes code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}

	public ApiServiceException(APIErrorCodes code, Throwable e) {
		super(code.message(), e);
		this.code = code;
		this.message = code.message();
	}

	public ApiServiceException(APIErrorCodes code, String message, Throwable e) {
		super(message, e);
		this.code = code;
		this.message = message;
	}

	public APIErrorCodes getCode() {
		return code;
	}

	@Override
	public String toString() {
		return "DynamicException [message=" + message + ", code=" + code + "]";
	}

}
