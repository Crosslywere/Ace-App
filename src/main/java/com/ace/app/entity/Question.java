package com.ace.app.entity;

import java.util.List;

import com.ace.app.dto.CreateQuestionDTO;
import com.ace.app.model.QuestionId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 28-May-2024
 */
@Getter
@Setter
@Entity
@Table( name = "questions" )
@NoArgsConstructor
@AllArgsConstructor
public class Question {

	@EmbeddedId
	private QuestionId questionId;

	@Column( length = 500, nullable = false )
	private String question;

	@Column( nullable = false )
	private List<String> options;

	private Short answerIndex;

	public Question( CreateQuestionDTO questionDTO, Paper paper ) {
		questionId = new QuestionId( paper, questionDTO.getNumber() );
		question = questionDTO.getQuestion();
		options = questionDTO.getOptions();
		answerIndex = questionDTO.getAnswerIndex();
	}

	public Paper getPaper() {
		if ( questionId == null ) {
			return null;
		}
		return questionId.getPaper();
	}

	public void setPaper( Paper paper ) {
		if ( questionId == null ) {
			questionId = new QuestionId();
		}
		questionId.setPaper( paper );
	}

	public Integer getNumber() {
		if ( questionId == null ) {
			return null;
		}
		return questionId.getNumber();
	}

	public void setNumber( Integer number ) {
		if ( questionId == null ) {
			questionId = new QuestionId();
		}
		questionId.setNumber( number );
	}
}
