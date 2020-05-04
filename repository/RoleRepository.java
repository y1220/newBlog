package it.course.myblog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Role;
import it.course.myblog.entity.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
	
	Optional<Role> findByName(RoleName roleName);
	
	Boolean existsByName(RoleName roleName);

}
