package com.ace.app.dto;

import com.ace.app.entity.Question;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateQuestionDTO extends BaseQuestionDTO {

	private Short answerIndex;

	public CreateQuestionDTO( Question question ) {
		super();
		super.number = question.getNumber();
		super.question = question.getQuestion();
		super.options = question.getOptions();
		this.answerIndex = question.getAnswerIndex();
	}
}
