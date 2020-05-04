package it.course.myblog.controller;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblog.entity.Credit;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.repository.CreditRepository;

@RestController
@RequestMapping("/credit")
public class CreditController {
	
	@Autowired
	CreditRepository creditRepository;
	
	@PostMapping("/insert-credit")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> insertCredit(@RequestBody Credit credit, HttpServletRequest request) {
		
		Optional<Credit> cr = creditRepository.findByCreditCodeAndEndDateIsNull(credit.getCreditCode());
		Credit c = new Credit();
		
		c.setCreditCode(credit.getCreditCode());
		c.setCreditDescription(credit.getCreditDescription());
		c.setCreditImport(credit.getCreditImport());
		c.setStartDate(credit.getStartDate());
		c.setEndDate(null);	
		if(cr.isPresent()) {
			cr.get().setEndDate(new Date());
		}
		
		creditRepository.save(cr.get());
		creditRepository.save(c);
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, "New Credit successfully created" , request.getRequestURI()), HttpStatus.OK);
		
	}
	
	@GetMapping("/all-credits")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> getAllCredits(HttpServletRequest request){
		
		
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, creditRepository.findByEndDateIsNullOrderByCreditCodeAsc() , request.getRequestURI()), HttpStatus.OK);
		
	}
	
	

}
