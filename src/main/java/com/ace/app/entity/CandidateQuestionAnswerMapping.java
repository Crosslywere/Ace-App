package com.ace.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 20-May-2024
 */
@Getter
@Setter
@Entity
@Table( name = "candidate_question_answer_map" )
@NoArgsConstructor
@AllArgsConstructor
public class CandidateQuestionAnswerMapping {

	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private Long candidateQuestionAnswerId;

	@ManyToOne
	private Candidate candidate;

	@ManyToOne
	private Question question;

	private Integer answerIndex;
}
