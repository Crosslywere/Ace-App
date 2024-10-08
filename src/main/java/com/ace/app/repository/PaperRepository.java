package com.ace.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ace.app.entity.Exam;
import com.ace.app.entity.Paper;
import com.ace.app.model.PaperId;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Repository
public interface PaperRepository extends JpaRepository<Paper, PaperId> {

	default Optional<Paper> findById( String paperName, Exam exam ) {
		return findById( new PaperId( exam, paperName ) );
	}
}
