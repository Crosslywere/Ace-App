package com.ace.app.dto;

import com.ace.app.entity.CandidateQuestionAnswerMapper;

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

	public ExamQuestionDTO( CandidateQuestionAnswerMapper questionAnswerMap ) {
		this.refNumber = questionAnswerMap.getQuestion().getNumber();
		this.number = questionAnswerMap.getCandidateQuestionAnswerId().getCandidateQuestionNumber();
		this.answerIndex = questionAnswerMap.getAnswerIndex();
	}
}
