package it.course.myblog.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import it.course.myblog.entity.Comment;
import it.course.myblog.entity.Post;
import it.course.myblog.entity.RoleName;
import it.course.myblog.entity.Users;
import it.course.myblog.payload.request.CommentRequest;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.payload.response.CountCommentGroupByPostResponse;
import it.course.myblog.repository.CommentRepository;
import it.course.myblog.repository.CreditRepository;
import it.course.myblog.repository.PostRepository;
import it.course.myblog.repository.UserRepository;
import it.course.myblog.security.UserPrincipal;
import it.course.myblog.service.UserService;

@RestController
@RequestMapping("/comment")
public class CommentController {
	
	@Autowired
	CommentRepository commentRepository;
	
	@Autowired
	PostRepository postRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CreditRepository creditRepository;
	
	
	@PostMapping("/insert-comment")
	@PreAuthorize("hasRole('EDITOR') or hasRole('READER')")
	public ResponseEntity<ApiResponseCustom> createComment(@RequestBody CommentRequest commentRequest, HttpServletRequest request){
			
		Optional<Post> p = postRepository.findById(commentRequest.getId());
		if(!p.isPresent())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 404, null, "Post not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
		
		// CONTROL IF THE POST IS DELUXE
		if(p.get().getCredit().getCreditImport() > 0) {
			
			// CONTROL IF THE USER IS LOGGED IN
			UserPrincipal up = UserService.getAuthenticatedUser(); 
				
			// EXCEPT ROLE_READER, THE OTHER ROLES CAN VIEW THE POST
			if(up.getAuthorities().stream().filter(g -> g.getAuthority().equals("ROLE_READER")).count() > 0) {
			
				Optional<Users> u = userRepository.findById(up.getId());
				Set<Post> postBoughtList = u.get().getPosts();
				
				// CONTROL IF LOGGED USER BOUGHT THE POST
				if( postBoughtList.stream().filter(post -> (post.getId() == p.get().getId()) ).count() == 0) {
					
					return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 200, null, "You need to buy the relative Post in order to insert a comment", request.getRequestURI()),
							HttpStatus.FORBIDDEN);
					
				} 
			}
			
		}
		
		Comment c = new Comment();	
		c.setReview(commentRequest.getReview());
		c.setPost(p.get());
		c.setCredit(creditRepository.findByEndDateIsNullAndCreditCodeStartingWith("C").get());
		
		commentRepository.save(c);
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, "New Comment successfully created" , request.getRequestURI()), HttpStatus.OK);
		
	}
	
	@PutMapping("/publish-comment/{id}")
	@PreAuthorize("hasRole('MANAGING_EDITOR')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> publishComment(@PathVariable Long id, HttpServletRequest request){
		
		Optional<Comment> c = commentRepository.findById(id);
		
		if(!c.isPresent())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 404, null, "Comment not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
		
		if(c.get().isVisible())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 401, null, "The Comment was just published", request.getRequestURI()), HttpStatus.FORBIDDEN);
			
		c.get().setVisible(true);
		
		Optional<Users> u = userRepository.findById(c.get().getCreatedBy());
		
		// CREDITS ASSIGNED ONLY IF THE USER ROLE IS 'ROLE_READER'
		if( u.get().getRoles().stream().filter(r -> r.getName().equals(RoleName.ROLE_READER)).count() > 0) {
		
			u.get().setCredit(u.get().getCredit() + c.get().getCredit().getCreditImport());
			userRepository.save(u.get());
			
		}
			
		commentRepository.save(c.get());
		

		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, "Comment successfully published" , request.getRequestURI()), HttpStatus.OK);
	}
	
	@PutMapping("/unpublish-comment/{id}")
	@PreAuthorize("hasRole('MANAGING_EDITOR')")
	public ResponseEntity<ApiResponseCustom> unpublishComment(@PathVariable Long id, HttpServletRequest request){
		
		Optional<Comment> c = commentRepository.findById(id);
		
		if(!c.isPresent())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 404, null, "Comment not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
		
		if(!c.get().isVisible())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 401, null, "The Comment was just unpublished", request.getRequestURI()), HttpStatus.FORBIDDEN);
			
		c.get().setVisible(false);
		
		Optional<Users> u = userRepository.findById(c.get().getCreatedBy());
		
		// CREDITS SUBTRACTED ONLY IF THE USER ROLE IS 'ROLE_READER'
		if( u.get().getRoles().stream().filter(r -> r.getName().equals(RoleName.ROLE_READER)).count() > 0) {
		
			u.get().setCredit(u.get().getCredit() - c.get().getCredit().getCreditImport());
			userRepository.save(u.get());
			
		}
		
		commentRepository.save(c.get());

		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, "Comment successfully unpublished" , request.getRequestURI()), HttpStatus.OK);
	}
	
	
	@PutMapping("/update-comment")
	@PreAuthorize("hasRole('EDITOR') or hasRole('READER')")
	public ResponseEntity<ApiResponseCustom> updateComment(@RequestBody CommentRequest commentRequest, HttpServletRequest request){
		
		Optional<Comment> c = commentRepository.findById(commentRequest.getId());
		
		if(!c.isPresent())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 404, null, "Comment not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
		
		if(!c.get().isVisible())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 401, null, "The Comment is unpublished", request.getRequestURI()), HttpStatus.FORBIDDEN);
			
		// RECOVER FROM SECURITY CONTEXT THE USER LOGGED IN
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		
		// THE COMMENT IS UPDATABLE ONLY IF THE USER LOGGED IN IS EQUAL TO COMMENT CREATEDBY
		if(userPrincipal.getId() == c.get().getCreatedBy()) {
			c.get().setReview(commentRequest.getReview());
			c.get().setVisible(false);
			commentRepository.save(c.get());
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, "Comment successfully updated" , request.getRequestURI()), HttpStatus.OK);
		} else {
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 401, null, "The Comment is not updatable by you", request.getRequestURI()), HttpStatus.FORBIDDEN);
		}
		
	}
	
	@GetMapping("/count-comments-group-by-post")
	@PreAuthorize("hasRole('ADMIN')")
	@ApiOperation(value="Count comments group by post", response = ResponseEntity.class)
	public ResponseEntity<ApiResponseCustom> countCommentsGroupByPost(HttpServletRequest request){
		
		// FIND ALL PUBLISHED POSTS
		List<Post> ps = postRepository.findAllByIsVisibleTrue();
		
		List<CountCommentGroupByPostResponse> countCommentsGroupByPostList = new ArrayList<CountCommentGroupByPostResponse>();
		
		for(Post p : ps) {
			// FIND PUBLISHED COMMENT FOREACH POST
			List<Comment> cs = p.getComments().stream().filter(c -> c.isVisible()).collect(Collectors.toList());
			p.setComments(cs);
			// COUNT PUBLISHED COMMENTS
			long countComments = p.getComments().stream().count();
			countCommentsGroupByPostList.add(new CountCommentGroupByPostResponse(p.getId(), p.getTitle(), countComments));
		}
		
		// SORTING LIST BY COUNT DESCENDING
		List<CountCommentGroupByPostResponse> countCommentsGroupByPostSortedList = countCommentsGroupByPostList.stream()
			.sorted(Comparator.comparingLong(CountCommentGroupByPostResponse::getCountcomments)
			.reversed())
			.collect(Collectors.toList());
			
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, countCommentsGroupByPostSortedList, request.getRequestURI()), HttpStatus.OK);
		
	}
	

	@PutMapping("/publish-comments/{ids}")
	@PreAuthorize("hasRole('MANAGING_EDITOR')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> publishComments(@PathVariable List<Long> ids, HttpServletRequest request){
		
		List<Comment> cs = commentRepository.findByIdIn(ids);
		List<Users> us = new ArrayList<Users>();
		
		for(Comment comment : cs) {
			Users u = userRepository.findById(comment.getCreatedBy()).get();
			if( u.getRoles().stream().filter(r -> r.getName().equals(RoleName.ROLE_READER)).count() > 0) {
				u.setCredit(u.getCredit() + comment.getCredit().getCreditImport());
				us.add(u);
			}
		}
		
		cs.forEach(c -> c.setVisible(true));
		
		commentRepository.saveAll(cs);		
		userRepository.saveAll(us);
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, "All selected comments have been published", request.getRequestURI()), HttpStatus.OK);
		
	}
	
	@PutMapping("/unpublish-comments/{ids}")
	@PreAuthorize("hasRole('MANAGING_EDITOR')")
	public ResponseEntity<ApiResponseCustom> unpublishComments(@PathVariable List<Long> ids, HttpServletRequest request){
		
		List<Comment> cs = commentRepository.findByIdIn(ids);
		List<Users> us = new ArrayList<Users>();
		
		for(Comment comment : cs) {
			Users u = userRepository.findById(comment.getCreatedBy()).get();
			if( u.getRoles().stream().filter(r -> r.getName().equals(RoleName.ROLE_READER)).count() > 0) {
				u.setCredit(u.getCredit() - comment.getCredit().getCreditImport());
				us.add(u);
			}
		}
		cs.forEach(c -> c.setVisible(false));
		
		commentRepository.saveAll(cs);	
		userRepository.saveAll(us);
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 200, null, "All selected comments have been unpublished", request.getRequestURI()), HttpStatus.OK);
		
	}
	
	
	
}
