package com.kirtesh.subwaytransition.controllers;

import com.kirtesh.subwaytransition.exception.ApiServiceException;
import com.kirtesh.subwaytransition.response.CustomError;
import com.kirtesh.subwaytransition.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/*
 * author : kirtesh
 */
public class BaseController {

	private Logger logger = LoggerFactory.getLogger(BaseController.class);

	@ExceptionHandler({ Exception.class })
	@ResponseBody
	public ResponseEntity<Response<Object>> handleInternalServerError(Exception ex) {
		logger.error("Exception occurred while processing request", ex);
		return new ResponseEntity<Response<Object>>(Response.internalServerError(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ ApiServiceException.class })
	@ResponseBody
	public ResponseEntity<Response<Object>> handleAPIException(ApiServiceException ex) {
		CustomError error = new CustomError();
		error.setCode(String.valueOf(ex.getCode().code()));
		error.setMessage(ex.getMessage());
		return new ResponseEntity<Response<Object>>(Response.failureResponseWithError(error), HttpStatus.OK);
	}

	@ResponseBody
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Response<Object>> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();
		List<CustomError> errors = new ArrayList<>();
		for (FieldError fieldError : fieldErrors) {
			CustomError error = new CustomError();
			error.setCode(fieldError.getCode());
			error.setMessage(fieldError.getDefaultMessage());
			error.setCategory(fieldError.getField());
			errors.add(error);
		}
		return new ResponseEntity<Response<Object>>(Response.failureResponse(errors), HttpStatus.BAD_REQUEST);
	}
}
