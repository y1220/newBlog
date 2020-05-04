package it.course.myblog.repository;

import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Credit;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long>{
	
	Optional<Credit> findByCreditCodeAndEndDateIsNull(String creditCode);
	
	Optional<Credit> findByCreditCode(String creditCode);
	
	Optional<Credit> findByEndDateIsNullAndCreditCodeStartingWith(String creditCode);
	
	List<Credit> findByEndDateIsNullOrderByCreditCodeAsc();
	

}
