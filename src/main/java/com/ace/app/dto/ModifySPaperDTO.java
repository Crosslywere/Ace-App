package com.ace.app.dto;

import java.util.ArrayList;
import java.util.List;

import com.ace.app.entity.Paper;
import com.ace.app.entity.Question;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 17-June-2024
 */
@Getter
@Setter
@NoArgsConstructor
public class ModifySPaperDTO extends BasePaperDTO {

	private List<Integer> questionNumberIndex;
	private List<ModifySQuestionDTO> modifyQuestions;

	public ModifySPaperDTO( Paper paper ) {
		super();
		super.name = paper.getName();
		super.questionsPerCandidate = paper.getQuestionsPerCandidate();
		super.manditory = paper.getManditory();
		this.modifyQuestions = new ArrayList<>();
		paper.getQuestions().forEach( question -> {
			modifyQuestions.add( new ModifySQuestionDTO( question ) );
		} );
		buildQuestionNumberIndex();
	}

	public ModifySPaperDTO( CreatePaperDTO paperDTO ) {
		super();
		super.name = paperDTO.getName();
		super.questionsPerCandidate = paperDTO.getQuestionsPerCandidate();
		super.manditory = paperDTO.getManditory();
		this.modifyQuestions = new ArrayList<>();
		paperDTO.getQuestions().forEach( questionDTO -> {
			modifyQuestions.add( new ModifySQuestionDTO( questionDTO ) );
		} );
		buildQuestionNumberIndex();
	}

	/**
	 * 
	 * @param questions
	 */
	public void appendAll( List<CreateQuestionDTO> questions ) {
		if ( questionNumberIndex == null || questionNumberIndex.isEmpty() ) {
			buildQuestionNumberIndex();
		}
		for ( CreateQuestionDTO question : questions ) {
			if ( question != null ) {
				int index = questionNumberIndex.indexOf( question.getNumber() );
				if ( index > -1 ) {
					modifyQuestions.set( index, new ModifySQuestionDTO( new Question( question, null ) ) );
				} else {
					questionNumberIndex.add( question.getNumber() );
					modifyQuestions.add( new ModifySQuestionDTO( new Question( question, null ) ) );
				}
			}
		}
	}

	private void buildQuestionNumberIndex() {
		questionNumberIndex = new ArrayList<>();
		for ( ModifySQuestionDTO modifyQuestion : modifyQuestions ) {
			if ( questionNumberIndex.contains( modifyQuestion.getNumber() ) ) {
				System.err.println( "Duplicate numbered questions detected in " + super.getName() + " (" + modifyQuestion.getNumber() + ")" );
				break;
			}
			questionNumberIndex.add( modifyQuestion.getNumber() );
		}
	}
}
