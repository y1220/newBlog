package it.course.myblog.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Post;


@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
	
	// List<Post> findAllOrderByUpdatedAtDesc();
	
	long countByCreatedBy(Long id);
	
	List<Post> findAllByIsVisibleTrue(Sort sort);
	List<Post> findAllByIsVisibleTrue();
	
	List<Post> findByIdIn(List<Long> ids);

	List<Post> findByCreatedBy(Long id);
	
	List<Post> findAll();
	
	List<Post> findByIsVisibleTrueAndCreatedBy(Long id);
	
	List<Post> findTop2ByIsVisibleTrueOrderByAvgRatingDesc();
	
	List<Post> findByIsVisibleTrueAndContentContaining(String keyword);
	
	Page<Post> findAllByIsVisibleTrue(Pageable pageable);
	
	Page<Post> findByIdIn(Set<Long> ids, Pageable pageable);
	
	
	

}
