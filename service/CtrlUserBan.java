package it.course.myblog.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import it.course.myblog.entity.Blacklist;
import it.course.myblog.entity.Users;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.repository.BlacklistRepository;

@Service
public class CtrlUserBan {
	
	@Autowired
	BlacklistRepository blacklistRepository;
	
	
	public Optional<Blacklist> isBanned(Users u) {
		
		return blacklistRepository.findTopByBlacklistedUntilAfterAndUserOrderByBlacklistedUntilDesc(LocalDate.now(), u);
		
	}

}
