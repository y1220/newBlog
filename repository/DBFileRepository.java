package it.course.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.DBFile;

@Repository
public interface DBFileRepository extends JpaRepository<DBFile, Long>{

}
