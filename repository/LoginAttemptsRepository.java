package it.course.myblog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.LoginAttempts;

@Repository
public interface LoginAttemptsRepository extends JpaRepository<LoginAttempts, Long> {

	Optional<LoginAttempts> findTop1ByUserIdOrderByUpdatedAtDesc(Long id);

	Optional<LoginAttempts> findByIp(String ip);

}
