package it.course.myblog.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Role;
import it.course.myblog.entity.RoleName;
import it.course.myblog.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>{
	
	Optional<Users> findByUsername(String username);
	Optional<Users> findByEmail(String email);
	Optional<Users> findById(Long id);
	Optional<Users> findByIdAndRolesIn (Long Id, List<RoleName> role);
	Optional<Users> findByIdentifierCode(String identifier);
	Optional<Users> findByUsernameOrEmail(String username, String email);
	
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
	
	
	
	//not optional, use list -> return multiple values
	List<Users> findByLastnameLike(String lastname);
	
	List<Users> findUserByHasNewsletterTrue();
	
	long count(); 
	
}
