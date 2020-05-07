package it.course.myblog.controller;

import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblog.entity.Post;
import it.course.myblog.entity.Users;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.repository.PostRepository;
import it.course.myblog.repository.UserRepository;
import it.course.myblog.security.UserPrincipal;
import it.course.myblog.service.FileService;
import it.course.myblog.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/download")
public class FileController {

	@Autowired
	PostRepository postRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Autowired
	FileService fileService;

	@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGING_EDITOR') or hasRole('READER') or hasRole('EDITOR')")
	@GetMapping("/pdf/{postId}")
	public ResponseEntity<?> download(@PathVariable Long postId, HttpServletRequest request) {

		Optional<Post> post = postRepository.findById(postId);

		if (!post.isPresent()) {
			log.error("Post {} not found", postId);
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(), 404, null, "Post not found", request.getRequestURI()),
					HttpStatus.NOT_FOUND);
		}

		if (!post.get().isVisible()) {
			log.error("Post {} not published", postId);
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(), 401, null, "Post not published", request.getRequestURI()),
					HttpStatus.FORBIDDEN);
		}

		if (post.get().getCredit().getCreditImport() > 0) {
			UserPrincipal up = UserService.getAuthenticatedUser();

			if (up.getAuthorities().size() < 2) {

				if (up.getAuthorities().stream().filter(g -> g.getAuthority().equals("ROLE_READER")).count() > 0) {

					Optional<Users> user = userRepository.findById(up.getId());
					Set<Post> ownedPosts = user.get().getPosts();

					if (ownedPosts.stream().filter(p -> (p.getId() == post.get().getId())).count() == 0) {
						log.error("Post {} not owned", postId);
						return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 401, null,
								"Post not owned", request.getRequestURI()), HttpStatus.FORBIDDEN);
					}
				}
			}
		}

		InputStream pdfFile = null;
		ResponseEntity<InputStreamResource> response = null;
		try {
			pdfFile = fileService.createPdfFromPost(post.get());
			// Set headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("application/pdf"));
			headers.add("Access-Control-Allow-Origin", "*");
			headers.add("Access-Control-Allow-Methods", "GET");
			headers.add("Access-Control-Allow-Headers", "Content-Type");
			headers.add("Content-disposition",
					"inline; filename=" + post.get().getTitle().replaceAll(" ", "_") + ".pdf");
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");

			response = new ResponseEntity<InputStreamResource>(new InputStreamResource(pdfFile), headers,
					HttpStatus.OK);
		} catch (Exception e) {
			log.error("Some error occurs in pdf generation: " + e.getMessage());
			response = new ResponseEntity<InputStreamResource>(
					new InputStreamResource(null, "Some error occurs in pdf generation: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;

	}



}
