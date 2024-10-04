package com.ace.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ace.app.entity.Candidate;
import com.ace.app.entity.CandidateQuestionAnswerMapper;
import com.ace.app.entity.Paper;
import com.ace.app.model.CandidateQuestionAnswerMapperId;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Repository
public interface CandidateQuestionAnswerMapperRepository extends JpaRepository<CandidateQuestionAnswerMapper, CandidateQuestionAnswerMapperId> {

	default Optional<CandidateQuestionAnswerMapper> findById( Integer questionNumber, Candidate candidate, Paper paper ) {
		return findById( new CandidateQuestionAnswerMapperId( questionNumber, candidate, paper ) );
	}

}
