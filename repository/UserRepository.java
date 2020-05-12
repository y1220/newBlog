package it.course.myblog.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Role;
import it.course.myblog.entity.Users;


@Repository
public interface UserRepository extends JpaRepository<Users, Long>{
	
	Optional<Users> findByUsername(String username);
	Optional<Users> findByEmail(String email);
	Optional<Users> findById(Long id);
	Optional<Users> findByIdentifierCode(String identifier);
	Optional<Users> findByUsernameOrEmail(String username, String email);
	
	List<Users> findByRoles(Role r);

	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
	
	List<Users> findByLastnameLike(String lastname);
	List<Users> findUserByHasNewsletterTrue();

	Optional<Users> findByIdAndRolesIn(Long userId, Set<Role> roles);

	List<Users> findByRolesOrIdIn(Role role, List<Long> ids);

	long count();
	
	// Fabio
	@Query(value = "SELECT *" + "	FROM  users AS u" + "	WHERE u.id IN (SELECT p.created_by"
			+ "		FROM post AS p, users AS u" + "		WHERE u.id=p.created_by)" + "	OR u.id IN (SELECT u.id"
			+ "		FROM users AS u,roles AS r,user_roles AS ur " + "		WHERE u.id=ur.user_id AND"
			+ "		ur.role_id=r.id AND" + "		r.name='ROLE_EDITOR')", nativeQuery = true)
	List<Users> findByUsersWhoWrotePosts();
	
	@Query(value = "SELECT *" + " FROM  users AS u" + " WHERE u.id IN (SELECT c.created_by"
			+ " 	FROM comment AS c, users AS u" + " 	WHERE u.id=c.created_by)" + " OR u.id IN (SELECT u.id"
			+ " 	FROM users AS u,roles AS r, user_roles AS ur " + " 	WHERE u.id=ur.user_id AND"
			+ "    ur.role_id=r.id AND" + "    r.name='ROLE_READER')", nativeQuery = true)
	List<Users> findByUsersWhoWroteComments();



	
	
}
