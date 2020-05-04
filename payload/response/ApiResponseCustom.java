package it.course.myblog.payload.response;

import java.time.Instant;

public class ApiResponseCustom {
	
	private Instant timestamp;
	private int httpStatus;
	private String error;
	private Object message;
	private String path;
	
	public ApiResponseCustom(Instant timestamp, int httpStatus, String error, Object message, String path) {
		super();
		this.timestamp = timestamp;
		this.httpStatus = httpStatus;
		this.error = error;
		this.message = message;
		this.path = path;
	}
	public Instant getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
	public int getHttpStatus() {
		return httpStatus;
	}
	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
}
