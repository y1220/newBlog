package it.course.myblog.controller;

import java.time.Instant;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import it.course.myblog.payload.response.ApiResponseCustom;


@RestController
@ControllerAdvice
public class MaxFileUploadController {
	    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseCustom> handleAllExceptions(Exception ex, HttpServletResponse response) throws Exception {
	    if( ex instanceof MaxUploadSizeExceededException == false) {
	    	throw ex;
	    }else {
	    	long MAX_FILE_SIZE = 102400; // 100Kb
	    	long KILOBYTE = 1024L;
	    	ResponseEntity<ApiResponseCustom> responsePayload =
	    			new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(),
    					 HttpStatus.INTERNAL_SERVER_ERROR.value(), null,
    					 "The selected file is greater than "+MAX_FILE_SIZE / KILOBYTE+" Kb", null),
    					 HttpStatus.INTERNAL_SERVER_ERROR);
	      return responsePayload;
	    }
    }
    
}

