package it.course.myblog.controller;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblog.entity.Post;
import it.course.myblog.entity.Tag;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.repository.PostRepository;
import it.course.myblog.repository.TagRepository;

@RestController
@RequestMapping("/tags")
public class TagController {
	
	@Autowired
	TagRepository tagRepository;
	
	@Autowired
	PostRepository postRepository;
	
	@PostMapping("/insert-tag/{tagName}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> insertTag (@PathVariable String tagName, HttpServletRequest request){
		
		Optional<Tag> t =  tagRepository.findByTagName(tagName);
		if(t.isPresent()) { // IF THE TAG NAME EXISTS -> UPDATE
			t.get().setTagName(tagName);
			tagRepository.save(t.get());
		} else {
			tagRepository.save(new Tag(tagName)); // ELSE THE NEW TAG WILL BE ADDED - > INSERT
		}
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, "New Tag has been added", request.getRequestURI()), HttpStatus.OK);
		
		
	}
	
	@GetMapping("/get-all-tags")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> getAllTags (HttpServletRequest request){
		
		List<Tag> allTags = tagRepository.findAll();
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, allTags, request.getRequestURI()), HttpStatus.OK);
	}
	

	@DeleteMapping("/delete-tag/{tagName}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> deleteTag (@PathVariable String tagName, HttpServletRequest request){
		
		Optional<Tag> t =  tagRepository.findByTagName(tagName);
		if(!t.isPresent())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 404, null, "Tag not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
		
		List<Post> ps = postRepository.findAll();
		
		for(Post p : ps) {
			// in tag class we defined as set so don't use list be attention!
			Set<Tag> ts = new HashSet<>(p.getTags());
			if(ts.contains(t.get())) {
				ts.remove(t.get());
				p.setTags(ts);
				postRepository.save(p);
			}	
		}
		tagRepository.delete(t.get());
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, "The tag "+tagName+" has been deleted", request.getRequestURI()), HttpStatus.OK);
		
	}
	
}
