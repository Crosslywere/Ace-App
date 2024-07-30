package com.ace.app.entity;

import com.ace.app.model.CandidateQuestionAnswerMapperId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Getter
@Setter
@Entity
@Table( name = "candidate_question_answer_mapper" )
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode( exclude = "answerIndex" )
public class CandidateQuestionAnswerMapper {

	@EmbeddedId
	private CandidateQuestionAnswerMapperId candidateQuestionAnswerId;

	@ManyToOne
	private Question question;

	private Integer answerIndex;

	public CandidateQuestionAnswerMapper( Integer candidateQuestionNumber, Candidate candidate, Paper paper, Question question ) {
		this.candidateQuestionAnswerId = new CandidateQuestionAnswerMapperId( candidateQuestionNumber, candidate, paper );
		this.question = question;
	}

	public CandidateQuestionAnswerMapper( Integer candidateQuestionNumber, Candidate candidate, Question question ) {
		this.candidateQuestionAnswerId = new CandidateQuestionAnswerMapperId( candidateQuestionNumber, candidate, question.getPaper() );
		this.question = question;
	}

	public Integer getCandidateQuestionNumber() {
		if ( candidateQuestionAnswerId == null ) {
			return null;
		}
		return candidateQuestionAnswerId.getCandidateQuestionNumber();
	}

	public void setCandidateQuestionNumber( Integer candidateQuestionNumber ) {
		if ( candidateQuestionAnswerId == null ) {
			candidateQuestionAnswerId = new CandidateQuestionAnswerMapperId();
		}
		candidateQuestionAnswerId.setCandidateQuestionNumber( candidateQuestionNumber );
	}

	public String getPaperName() {
		if ( candidateQuestionAnswerId == null ) {
			return null;
		}
		return candidateQuestionAnswerId.getPaperName();
	}

	public void setPaper( Paper paper ) {
		if ( candidateQuestionAnswerId == null ) {
			candidateQuestionAnswerId = new CandidateQuestionAnswerMapperId();
		}
		candidateQuestionAnswerId.setPaper( paper );
	}

	public void setCandidate( Candidate candidate ) {
		if ( candidateQuestionAnswerId == null ) {
			candidateQuestionAnswerId = new CandidateQuestionAnswerMapperId();
		}
		candidateQuestionAnswerId.setCandidate( candidate );
	}
}
