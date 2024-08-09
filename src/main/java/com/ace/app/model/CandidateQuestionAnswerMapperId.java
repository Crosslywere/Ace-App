package com.ace.app.model;

import java.io.Serializable;

import com.ace.app.entity.Candidate;
import com.ace.app.entity.Paper;

import jakarta.persistence.Embeddable;
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
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CandidateQuestionAnswerMapperId implements Serializable {

	private Long examId;

	private Integer candidateQuestionNumber;

	private String candidateField1;

	private String candidateField2;

	private String paperName;

	public CandidateQuestionAnswerMapperId( Integer candidateQuestionNumber, Candidate candidate, Paper paper ) {
		this.examId = candidate.getExam().getExamId();
		this.candidateQuestionNumber = candidateQuestionNumber;
		this.candidateField1 = candidate.getField1();
		this.candidateField2 = candidate.getField2();
		this.paperName = paper.getName();
	}

	public void setCandidate( Candidate candidate ) {
		this.candidateField1 = candidate.getField1();
		this.candidateField2 = candidate.getField2();
	}

	public void setPaper( Paper paper ) {
		this.paperName = paper.getName();
	}
}
