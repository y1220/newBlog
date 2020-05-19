package it.course.myblog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.course.myblog.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{
	
	List<Comment> findByIdIn(List<Long> ids);
	
	List<Comment> findByIsVisibleTrueAndCreatedBy(Long id);
	
	List<Comment> findByCreatedBy(Long id);
	
	long countByCreatedBy(Long userId);
	
	List<Comment> findByIsVisibleFalseAndCreatedBy(Long userId);

}
