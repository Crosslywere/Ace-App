package com.ace.app.model;

import java.io.Serializable;

import com.ace.app.entity.Candidate;
import com.ace.app.entity.Paper;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CandidateQuestionAnswerMapperId implements Serializable {

	private Integer candidateQuestionNumber;

	@ManyToOne
	private Candidate candidate;

	@ManyToOne
	private Paper paper;
}
