package it.course.myblog.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Post;
import it.course.myblog.entity.Rating;
import it.course.myblog.entity.RatingUserPostCompositeKey;
import it.course.myblog.entity.Users;

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingUserPostCompositeKey>{
	
	List<Rating> findByRatingUserPostCompositeKeyPostId(Post post);
	
	List<Rating> findByRatingUserPostCompositeKeyUserId(Users user);
	
	// Native query
	/*
	@Query(value="SELECT r.rating, r.post_id, r.user_id, COUNT(r.post_id) as count_rate "
			+ "FROM  rating AS r "
			+ "INNER JOIN post AS p "
			+ "ON p.id=r.post_id "
			+ "WHERE p.is_visible=true "
			+ "GROUP BY r.post_id "
			+ "ORDER BY count_rate DESC "
			+ "LIMIT 2", 
			nativeQuery=true)
	List<Rating> findByTwoMaxNumbersOfRates();
	*/
	
	// JPQL
	@Query(value="SELECT r, COUNT(r) as countRate "
			+ "FROM Rating r "
			+ "INNER JOIN Post AS p ON p.id=r.ratingUserPostCompositeKey.postId.id "
			+ "WHERE p.isVisible=true "
			+ "GROUP BY r.ratingUserPostCompositeKey.postId.id "
			+ "ORDER BY countRate DESC ")
	List<Rating> findByTwoMaxNumbersOfRates();
	
	
	
	Long countByRatingUserPostCompositeKeyPostId(Post post);
	
	
}
