package it.course.myblog.payload.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class ApiResponseCustom {
	
	private Instant timestamp;
	private int httpStatus;
	private String error;
	private Object message;
	private String path;
	
	

}
