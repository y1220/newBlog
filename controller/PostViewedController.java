package it.course.myblog.controller;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblog.entity.Post;
import it.course.myblog.entity.PostViewed;
import it.course.myblog.payload.request.PostViewedRequest;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.payload.response.PostViewedResponse;
import it.course.myblog.repository.PostRepository;
import it.course.myblog.repository.PostViewedRepository;
import one.util.streamex.StreamEx;

@RestController
@RequestMapping("/postviewed")
public class PostViewedController {
	
	@Autowired
	PostViewedRepository postViewedRepository;
	
	@Autowired
	PostRepository postRepository;
	
	@GetMapping("/count-visited-posts")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> countVisitedPost(@RequestBody PostViewedRequest postViewedRequest, HttpServletRequest request){
		
		
		List<PostViewed> pvs = postViewedRepository.findByViewedStartBetween(postViewedRequest.getStartDate(), postViewedRequest.getEndDate());
		
		List<PostViewed> pvsFilteredByIp = StreamEx.of(pvs).distinct(PostViewed::getIp).toList();
		
		List<PostViewedResponse> pvrs = pvsFilteredByIp.stream().map(PostViewedResponse::create).collect(Collectors.toList());	
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, "Posts viewed from "+postViewedRequest.getStartDate()+" to "+postViewedRequest.getEndDate()+ ": "+pvs.size()+". Unique: "+pvrs.size(), request.getRequestURI()), HttpStatus.OK);
				
	}
	
	@GetMapping("/count-visits-by-posts")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> countVisitsByPost(@RequestBody PostViewedRequest postViewedRequest, HttpServletRequest request){
		
		Optional<Post> p =  postRepository.findById(postViewedRequest.getPostId());
		if (!p.isPresent()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 404, null, "Post not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
		
		List<PostViewed> pvs = postViewedRepository.findByViewedStartBetweenAndPost(postViewedRequest.getStartDate(), postViewedRequest.getEndDate(), p.get());
		
		List<PostViewed> pvsFilteredByIp = StreamEx.of(pvs).distinct(PostViewed::getIp).toList();
		
		List<PostViewedResponse> pvrs = pvsFilteredByIp.stream().map(PostViewedResponse::create).collect(Collectors.toList());	
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, "Post "+p.get().getTitle()+" viewed from "+postViewedRequest.getStartDate()+" to "+postViewedRequest.getEndDate()+ ": "+pvs.size()+". Unique: "+pvrs.size(), request.getRequestURI()), HttpStatus.OK);
					
	}
	
	@PutMapping("/set-post-viewer-end/{id}")
	public ResponseEntity<ApiResponseCustom> approvePost (@PathVariable Long id, HttpServletRequest request){
		
		Optional<PostViewed> pv = postViewedRepository.findById(id);
		
		if(!pv.isPresent())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 404, null, "Visualization Not Found", request.getRequestURI()), HttpStatus.NOT_FOUND);
		
		if(pv.get().getViewedEnd()!=null)
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 401, null, "Visualization already ended", request.getRequestURI()), HttpStatus.FORBIDDEN);
		
		pv.get().setViewedEnd(Instant.now());
		postViewedRepository.save(pv.get());
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, "The visualization is ended", request.getRequestURI()), HttpStatus.OK);
	}

}
