package it.course.myblog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Post;

@Repository
public interface PostPagedRepository extends PagingAndSortingRepository<Post, Long> {

	
	
}
