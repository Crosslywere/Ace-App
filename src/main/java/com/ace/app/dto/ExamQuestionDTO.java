package com.ace.app.dto;

import com.ace.app.entity.CandidateQuestionAnswerMapping;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExamQuestionDTO {

	private Integer refNumber;

	private Integer number;

	private Integer answerIndex;

	public ExamQuestionDTO( CandidateQuestionAnswerMapping questionAnswerMap ) {
		this.refNumber = questionAnswerMap.getQuestion().getNumber();
		this.number = questionAnswerMap.getCandidateQuestionNumber();
		this.answerIndex = questionAnswerMap.getAnswerIndex();
	}
}
