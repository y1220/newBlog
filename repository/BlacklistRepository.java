package it.course.myblog.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Blacklist;
import it.course.myblog.entity.BlacklistReason;
import it.course.myblog.entity.Post;
import it.course.myblog.entity.Users;


@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, Long>{
	
	List<Blacklist> findByUser(Users user);
	
	List<Blacklist> findByUserAndIsVerifiedTrue(Users user);
	
	List<Blacklist> findByIsVerifiedFalse();
	
	List<Blacklist> findByBlacklistedUntilBeforeOrBlacklistedUntilIsNotNull(LocalDate localdate);
	List<Blacklist> findByCommentIdInAndBlacklistedUntilAfter(List<Long> ids, LocalDate now);
	List<Blacklist> findByCommentIdIn(List<Long> ids);
	List<Blacklist> findByPostIdInAndCommentId(List<Long> ids, Long zero);
	
	/** @Query -> nativeQuery **/
	//@Query(value = "SELECT b.id, b.blacklisted_from, MAX(b.blacklisted_until) AS blacklisted_until, b.user_id, b.post_id, b.comment_id, b.blacklist_reason_id, b.reported_by, b.is_verified  "
	//		+ "FROM blacklist b WHERE blacklisted_until > ?1 GROUP BY b.user_id", nativeQuery = true)
	//List<Blacklist> bannedUserProfileList(LocalDate localdate);
	
	/** @Query -> JPQL syntax **/
	@Query(value = "SELECT new Blacklist(b.id, b.blacklistedFrom, MAX(b.blacklistedUntil) AS blacklistedUntil, b.user, b.post, b.commentId, b.blacklistReason, b.reporter, b.isVerified)  "
			+ "FROM Blacklist b WHERE b.blacklistedUntil > :now GROUP BY b.user")
	List<Blacklist> bannedUserProfileList(@Param("now") LocalDate localdate);
	
	// SELECT -> ORDER BY DESC -> limit 0,1
	Optional<Blacklist> findTopByBlacklistedUntilAfterAndUserOrderByBlacklistedUntilDesc(LocalDate now, Users userToFind);
	
	List<Blacklist> findByUserAndBlacklistedUntilIsNotNull(Users user);
	
	boolean existsByPostAndReporterAndCommentIdAndBlacklistReason(Post post, Users reporter, Long commentId, BlacklistReason blacklistReason);

	List<Blacklist> findAllByUserId(Long userId);

}
