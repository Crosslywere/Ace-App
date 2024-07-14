package com.ace.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ace.app.entity.CandidateQuestionAnswerMapper;
import com.ace.app.model.CandidateQuestionAnswerMapperId;

@Repository
public interface CandidateQuestionAnswerMapperRepository extends JpaRepository<CandidateQuestionAnswerMapper, CandidateQuestionAnswerMapperId> {
}
