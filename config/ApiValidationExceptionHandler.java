package it.course.myblog.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import it.course.myblog.exception.ApiErrorsView;
import it.course.myblog.exception.ApiFieldError;
import it.course.myblog.exception.ApiGlobalError;

@ControllerAdvice
public class ApiValidationExceptionHandler extends ResponseEntityExceptionHandler{
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request			
	){
		BindingResult bindingResult = ex.getBindingResult();
		
		List<ApiFieldError> apiFieldErrors = bindingResult
				.getFieldErrors()
				.stream()
				.map(fieldError -> new ApiFieldError(
					fieldError.getField(),
					fieldError.getCode(),
					fieldError.getDefaultMessage(),
					fieldError.getRejectedValue())						
				).collect(Collectors.toList());
		
		List<ApiGlobalError> apiGlobalErrors = bindingResult
				.getGlobalErrors()
				.stream()
				.map(globalError -> new ApiGlobalError(
						globalError.getCode())
				).collect(Collectors.toList());
		
		ApiErrorsView apiErrorsView = new ApiErrorsView(apiFieldErrors, apiGlobalErrors);
		
		return new ResponseEntity<>(apiErrorsView, HttpStatus.UNPROCESSABLE_ENTITY);
//		thomas code
//		  return new ResponseEntity<Object>(new ApiResponse( Instant.now(), 422,
//				    null, apiErrorsView, request.getDescription(false)), HttpStatus.UNPROCESSABLE_ENTITY);
//		
	}

}
