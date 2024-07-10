package com.ace.app.dto;

import java.util.ArrayList;
import java.util.List;

import com.ace.app.entity.Paper;

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
public final class CreatePaperDTO extends BasePaperDTO {

	private List<CreateQuestionDTO> questions = new ArrayList<>();

	public CreatePaperDTO( String name, boolean manditory, List<CreateQuestionDTO> questions ) {
		super();
		super.name = name;
		super.manditory = manditory;
		this.questions = questions;
	}

	public CreatePaperDTO( Paper paper ) {
		super();
		super.name = paper.getName();
		super.manditory = paper.getManditory();
		super.questionsPerCandidate = paper.getQuestionsPerCandidate();
		paper.getQuestions().forEach( question -> {
			questions.add( new CreateQuestionDTO( question ) );
		} );
	}
}
