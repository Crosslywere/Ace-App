package com.ace.app.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.ace.app.dto.BaseCandidateDTO;
import com.ace.app.dto.ModifyOCandidateDTO;
import com.ace.app.model.CandidateId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Candidate class represents a candidate that can take an exam.
 * 
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Getter
@Setter
@Builder
@Entity
@Table( name = "candidates" )
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {

	@EmbeddedId
	private CandidateId candidateId;

	@Builder.Default
	@Column( nullable = false )
	private Boolean hasLoggedIn = false;

	@Builder.Default
	@Column( nullable = false )
	private Float timeUsed = 0.0f;

	@Builder.Default
	@Column( nullable = false )
	private Boolean submitted = false;

	@OneToMany( cascade = CascadeType.ALL, orphanRemoval = true )
	private List<CandidateQuestionAnswerMapper> answerMapper;

	private String email;

	private String phoneNumber;

	private String state;

	private String firstname;

	private String lastname;

	private String othername;

	@Builder.Default
	private Integer score = -1;
	
	@Builder.Default
	private List<String> papernames = new ArrayList<>();

	private LocalTime timeIn;

	private LocalTime lastUpdate;

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

	public void setSubmitted( boolean submitted ) {
		this.submitted = submitted;
		if ( submitted ) {
			hasLoggedIn = true;
		}
	}

	public static int score( Candidate candidate ) {
		int score = 0;
		// for ( CandidateQuestionAnswerMapper answerMapper : candidate.getAnswerMapper() ) {
		// 	if ( answerMapper.isCorrect() ) {
		// 		score++;
		// 	}
		// }
		for ( String papername : candidate.getPapernames() ) {
			score += score( candidate, papername );
		}
		return score;
	}

	public static int score( Candidate candidate, String papername ) {
		int score = 0;
		for ( var answerMapper : candidate.getAnswerMapper() ) {
			if ( answerMapper.getPaperName().equals( papername ) && answerMapper.isCorrect() ) {
				score++;
			}
		}
		return score;
	}

	public static int compareScore( Candidate c1, Candidate c2 ) {
		c1.setScore( Candidate.score( c1 ) );
		c2.setScore( Candidate.score( c2 ) );
		return c1.getScore().compareTo( c2.getScore() );
	}
}
