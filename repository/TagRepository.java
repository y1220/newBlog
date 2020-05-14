package it.course.myblog.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>{
	
	Optional<Tag> findByTagName(String tagName);
	
	Set<Tag> findByIdIn(Set<Long> tagIds);
	
	List<Tag> findBytagNameIn(List<String> tagNames);
	
	// name must not be duplicated
	Set<Tag> findBytagNameInOrderByTagName(List<String> tagNames);
	
 
}
