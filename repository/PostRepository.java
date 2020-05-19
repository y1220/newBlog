package it.course.myblog.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import it.course.myblog.entity.Post;
import it.course.myblog.entity.Tag;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
	
	// List<Post> findAllOrderByUpdatedAtDesc();
	
	long countByCreatedBy(Long id);
	
	List<Post> findAllByIsVisibleTrue(Sort sort);
	List<Post> findAllByIsVisibleTrue();
	
	List<Post> findByCreatedBy(Long id);
	
	List<Post> findByIdIn(List<Long> ids);
	
	List<Post> findByIsVisibleTrueAndCreatedBy(Long id);
	
	List<Post> findTop2ByIsVisibleTrueOrderByAvgRatingDesc();
	
	List<Post> findByIsVisibleTrueAndContentContaining(String keyword);
	
	Page<Post> findAllByIsVisibleTrue(Pageable pageable);
	
	Page<Post> findByIdIn(Set<Long> ids, Pageable pageable);
	
	List<Post> findByIsVisibleFalseAndCreatedBy(Long userId);
	
	Set<Post> findByTagsInAndIsVisibleTrue(Set<Tag> tags); 
	
	// Fabio
	@Query(value = "SELECT COUNT*" + 
			"	FROM post AS p, blacklist AS bl" + 
			"	WHERE post.id = bl.post_id AND p.created_by=userId AND bl.comment_id=NULL AND bl.is_verified=1 AND bl.blacklist_until=NULL AND p.is_visible=1",
			nativeQuery=true)
	long findPostIsBanned(Long userId);
	
	@Query(value = "SELECT COUNT*" + 
			"	FROM post AS p" + 
			"	WHERE p.is_visible=1 AND p.created_by=id",
			nativeQuery=true)
	long findByIsVisibleTruecountByCreatedBy(Long id);
	
	
	

}
