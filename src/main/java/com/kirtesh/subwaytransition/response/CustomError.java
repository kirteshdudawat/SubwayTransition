package com.kirtesh.subwaytransition.response;

/**
 * Error message dto
 */
public class CustomError {

	private String message;
	private String code;
	private String category;

	public CustomError() {
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return code;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Error [message=" + message + ", code=" + code + "]";
	}

}
