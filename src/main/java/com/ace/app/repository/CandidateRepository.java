package com.ace.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ace.app.dto.BaseCandidateDTO;
import com.ace.app.entity.Candidate;
import com.ace.app.entity.Exam;
import com.ace.app.model.CandidateId;
import java.util.List;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Repository
public interface CandidateRepository extends JpaRepository<Candidate, CandidateId> {

	default Optional<Candidate> findById( BaseCandidateDTO candidateDTO, Exam exam ) {
		return findById( new CandidateId( exam, candidateDTO.getField1(), candidateDTO.getField2() ) );
	}

	default Optional<Candidate> findById( Exam exam, String field1, String field2 ) {
		return findById( new CandidateId( exam, field1, field2 ) );
	}

	@Query( """
		SELECT c
		FROM Candidate c
		WHERE c.candidateId.exam.examId = ?1 AND (LOWER(c.candidateId.field1) LIKE ?2% OR LOWER(c.candidateId.field2) LIKE ?2% OR LOWER(c.email) LIKE ?2% OR LOWER(CONCAT(c.firstname, ' ', c.lastname, ' ', c.othername)) LIKE ?2%)
	""" )
	List<Candidate> findByExamIdAndString( Long examId, String str );

	@Query( """
		SELECT c
		FROM Candidate c
		WHERE  LOWER(c.candidateId.field1) LIKE %?2% AND c.candidateId.exam.examId = ?1
	""" )
	List<Candidate> searchByExamIdAndField1( Long examId, String field1 );
}
