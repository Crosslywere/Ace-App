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
public class ModifySQuestionDTO extends CreateQuestionDTO {

	public ModifySQuestionDTO( Question question ) {
		super();
		super.number = question.getNumber();
		super.options = question.getOptions();
		super.question = question.getQuestion();
		super.setAnswerIndex( question.getAnswerIndex() );
	}

	public ModifySQuestionDTO( CreateQuestionDTO questionDTO ) {
		super();
		super.number = questionDTO.getNumber();
		super.options = questionDTO.getOptions();
		super.question = questionDTO.getQuestion();
		super.setAnswerIndex( questionDTO.getAnswerIndex() );
	}
}
