package it.course.myblog.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.BlacklistReason;

@Repository
public interface BlacklistReasonRepository extends JpaRepository<BlacklistReason, Long>{
	
	Optional<BlacklistReason> findByReason(String reason);
}
