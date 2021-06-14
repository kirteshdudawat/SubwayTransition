package com.kirtesh.subwaytransition.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Response wrapper class
 */
public class Response<T> {
	private boolean success;
	private T body;
	private List<CustomError> errors;

	private Response() {
	}

	public boolean isSuccess() {
		return success;
	}

	public T getBody() {
		return body;
	}

	public List<CustomError> getErrors() {
		return errors;
	}

	public static <T> Response<T> successResponse(T body) {
		Response<T> response = new Response<T>();
		response.success = true;
		response.body = body;
		return response;
	}

	public static Response<Object> successResponse() {
		Response<Object> response = new Response<Object>();
		response.success = true;
		return response;
	}

	public static <T> Response<T> failureResponse(List<CustomError> errors) {
		Response<T> response = new Response<T>();
		response.success = false;
		response.errors = errors;
		return response;
	}

	public static <T> Response<T> failureResponseWithBody(T body) {
		Response<T> response = new Response<T>();
		response.success = false;
		response.body = body;
		return response;
	}

	public static <T> Response<T> internalServerError() {
		Response<T> response = new Response<T>();
		response.success = false;
		response.errors = new ArrayList<CustomError>();
		CustomError error = new CustomError();
		error.setCode("server.error");
		error.setMessage("Internal Server Error Occured. Please contact system administrator");
		response.errors.add(error);
		return response;
	}

	public static <T> Response<T> failureResponseWithError(CustomError error) {
		Response<T> response = new Response<T>();
		response.success = false;
		response.errors = new ArrayList<CustomError>();
		response.errors.add(error);
		return response;
	}

	public static <T> Response<T> failureResponseWithErrorCodeAndMessage(String errorCode, String message, String category) {
		CustomError error = new CustomError();
		error.setCode(errorCode);
		error.setCategory(category);
		error.setMessage(message);

		Response<T> response = new Response<T>();
		response.success = false;
		response.errors = new ArrayList<CustomError>();
		response.errors.add(error);
		return response;
	}
}
