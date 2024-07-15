package com.ace.app.dto;

import java.util.ArrayList;
import java.util.List;

import com.ace.app.entity.Candidate;
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
public class ExamPaperDTO {
	
	private String name;

	private List<ExamQuestionDTO> questions;

	public ExamPaperDTO( Paper paper, Candidate candidate ) {
		this.name = paper.getName();
		this.questions = new ArrayList<>();
		candidate.getAnswerMapper().forEach( questionAnswerMap -> {
			if ( questionAnswerMap.getQuestion().getPaper().getName().equals( name ) ) {
				questions.add( new ExamQuestionDTO( questionAnswerMap ) );
			}
		} );
	}
}
