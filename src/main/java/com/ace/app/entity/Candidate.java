package com.ace.app.entity;

import java.util.ArrayList;
import java.util.List;

import com.ace.app.dto.BaseCandidateDTO;
import com.ace.app.dto.ModifyOCandidateDTO;
import com.ace.app.model.CandidateId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Candidate class represents a candidate that can write an exam.
 * 
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Getter
@Setter
@Entity
@Table( name = "candidates" )
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {

	@EmbeddedId
	private CandidateId candidateId;

	@ManyToMany
	private List<Paper> papers = new ArrayList<>();

	@Column( nullable = false )
	private Boolean hasLoggedIn = false;

	@Column( nullable = false )
	private Integer score = -1;

	@Column( nullable = false )
	private Float timeUsed = 0.0f;

	@Column( nullable = false )
	private Boolean submitted = false;

	@OneToMany( cascade = CascadeType.ALL, orphanRemoval = true )
	private List<CandidateQuestionAnswerMapper> answerMapper;

	public Candidate( BaseCandidateDTO candidateDTO, Exam exam ) {
		this.candidateId = new CandidateId( exam, candidateDTO.getField1(), candidateDTO.getField2() == null ? "" : candidateDTO.getField2() );
	}

	public Candidate( ModifyOCandidateDTO candidateDTO, Exam exam ) {
		this.candidateId = new CandidateId( exam, candidateDTO.getField1(), candidateDTO.getField2() == null ? "" : candidateDTO.getField2() );
		this.hasLoggedIn = candidateDTO.getHasLoggedIn();
		this.submitted = candidateDTO.getSubmitted();
	}

	public Exam getExam() {
		if ( candidateId == null ) {
			return null;
		}
		return candidateId.getExam();
	}

	public void setExam( Exam exam ) {
		if ( candidateId == null ) {
			candidateId = new CandidateId();
		}
		candidateId.setExam( exam );
	}

	public String getField1() {
		if ( candidateId == null ) {
			return null;
		}
		return candidateId.getField1();
	}

	public void setField1( String field ) {
		if ( candidateId == null ) {
			candidateId = new CandidateId();
		}
		candidateId.setField1( field );
	}

	public String getField2() {
		if ( candidateId == null ) {
			return null;
		}
		return candidateId.getField2();
	}

	public void setField2( String field ) {
		if ( candidateId == null ) {
			candidateId = new CandidateId();
		}
		candidateId.setField2( field );
	}

	public static void score( Candidate candidate ) {
		int score = 0;
		for ( CandidateQuestionAnswerMapper answerMapper : candidate.getAnswerMapper() ) {
			if ( answerMapper.isCorrect() ) {
				score++;
			}
		}
		candidate.setScore( score );
	}
}
