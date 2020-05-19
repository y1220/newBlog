package it.course.myblog.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Post;
import it.course.myblog.entity.PostViewed;


@Repository
public interface PostViewedRepository extends JpaRepository<PostViewed, Long>{
	
	
	List<PostViewed> findByViewedStartBetween(Instant viewedStart, Instant viewedEnd);
	
	List<PostViewed> findByViewedStartBetweenAndPost(Instant viewedStart, Instant viewedEnd, Post post);

	List<PostViewed> findByPost(Post p);
}
