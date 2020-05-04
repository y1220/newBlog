package it.course.myblog.controller;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblog.entity.Post;
import it.course.myblog.entity.Rating;
import it.course.myblog.entity.RatingUserPostCompositeKey;
import it.course.myblog.entity.Users;
import it.course.myblog.payload.request.RatingRequest;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.repository.PostRepository;
import it.course.myblog.repository.RatingRepository;
import it.course.myblog.repository.UserRepository;
import it.course.myblog.security.UserPrincipal;

@RestController
@RequestMapping("/rating")
public class RatingController {
	
	@Autowired
	RatingRepository ratingRepository;
	
	@Autowired
	PostRepository postRepository;
	
	@Autowired
	UserRepository userRepository;
	
	
	@PostMapping("/rate-post")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<ApiResponseCustom> ratePost(@RequestBody RatingRequest ratingRequest, HttpServletRequest request){
		
		Post p = postRepository.findById(ratingRequest.getPostId()).get();
		
		// RECOVER FROM SECURITY CONTEXT THE USER LOGGED IN
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Users u = userRepository.findById(userPrincipal.getId()).get();
		
		// CTRL IF RATING IS BETWEEN 1 AND 5
		if(ratingRequest.getRating() > 5 && ratingRequest.getRating() < 1)
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 403, null, "The rate must be between 1 and 5", request.getRequestURI()), HttpStatus.FORBIDDEN);
		
		// SAVE THE RATE
		Rating rr = new Rating(ratingRequest.getRating(), new RatingUserPostCompositeKey(p, u));
		ratingRepository.save(rr);
		
		// CALC AVERAGE BY POST
		List<Rating> ratingListByPost = ratingRepository.findByRatingUserPostCompositeKeyPostId(p);
		double average = ratingListByPost.stream()
				.mapToInt(Rating::getRating)
				.average()
				.getAsDouble();
		
		// SAVE AVERAGE INTO THE POST
		p.setAvgRating(average);
		postRepository.save(p);
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, "Rating saved", request.getRequestURI()), HttpStatus.OK);
		
	}
	

}
