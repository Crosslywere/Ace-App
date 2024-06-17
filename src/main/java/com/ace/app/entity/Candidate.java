package com.ace.app.entity;

import java.util.ArrayList;
import java.util.List;

import com.ace.app.dto.CreateCandidateDTO;
import com.ace.app.dto.ModifySCandidateDTO;
import com.ace.app.model.CandidateId;

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
 * @version 08-June-2024
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

	private Integer score = 0;

	@Column( nullable = false )
	private Float timeUsed = 0.0f;

	@Column( nullable = false )
	private Boolean submitted = false;

	@OneToMany
	private List<CandidateQuestionAnswerMapping> questionAnswerMapping;

	public Candidate( CreateCandidateDTO candidateDTO, Exam exam ) {
		this.candidateId = new CandidateId( exam, candidateDTO.getField1(), candidateDTO.getField2() );
	}

	public Candidate( ModifySCandidateDTO candidateDTO, Exam exam ) {
		this.candidateId = new CandidateId( exam, candidateDTO.getField1(), candidateDTO.getField2() );
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
}
