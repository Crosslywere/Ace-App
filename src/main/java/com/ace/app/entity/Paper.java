package com.ace.app.entity;

import java.util.ArrayList;
import java.util.List;

import com.ace.app.dto.CreatePaperDTO;
import com.ace.app.dto.ModifySPaperDTO;
import com.ace.app.model.PaperId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Getter
@Setter
@Entity
@Table( name = "papers" )
@NoArgsConstructor
@AllArgsConstructor
public class Paper {

	@EmbeddedId
	private PaperId paperId;

	@Column( nullable = false )
	private Integer questionsPerCandidate;

	@OneToMany( cascade = CascadeType.ALL, mappedBy = "questionId.paper", orphanRemoval = true )
	private List<Question> questions;

	private Boolean manditory;

	public Paper( CreatePaperDTO paperDTO, Exam exam ) {
		paperId = new PaperId( exam, paperDTO.getName() );
		questionsPerCandidate = paperDTO.getQuestionsPerCandidate();
		manditory = paperDTO.getManditory();
		questions = new ArrayList<>();
		paperDTO.getQuestions().forEach( questionDTO -> {
			questions.add( new Question( questionDTO, this ) );
		} );
	}

	public Paper( ModifySPaperDTO paperDTO, Exam exam ) {
		paperId = new PaperId( exam, paperDTO.getName() );
		questionsPerCandidate = paperDTO.getQuestionsPerCandidate();
		manditory = paperDTO.getManditory();
		questions = new ArrayList<>();
		paperDTO.getModifyQuestions().forEach( questionDTO -> {
			questions.add( new Question( questionDTO, this ) );
		} );
	}

	public Exam getExam() {
		if ( paperId == null ) {
			return null;
		}
		return paperId.getExam();
	}

	public void setExam( Exam exam ) {
		if ( paperId == null ) {
			paperId = new PaperId();
		}
		paperId.setExam( exam );
	}

	public String getName() {
		if ( paperId == null ) {
			return null;
		}
		return paperId.getName();
	}

	public void setName( String name ) {
		if ( paperId == null ) {
			paperId = new PaperId();
		}
		paperId.setName( name );
	}

	public void append( List<Question> otherQuestions ) {
		otherQuestions.forEach( question -> {
			if ( question.getNumber() > questions.getLast().getNumber() ) {
				questions.add( question );
			} else {
				boolean handled = false;
				for ( int i = 0; i < questions.size(); i++ ) {
					if ( questions.get( i ).getNumber() == question.getNumber() ) {
						questions.set( i, question );
						handled = true;
						break;
					}
				}
				if ( !handled ) {
					questions.add( question );
				}
			}
		} );
	}
}
