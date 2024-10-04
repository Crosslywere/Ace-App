package com.ace.app.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ace.app.entity.Candidate;
import com.ace.app.entity.CandidateQuestionAnswerMapper;
import com.ace.app.entity.Exam;
import com.ace.app.entity.Paper;
import com.ace.app.model.CandidateId;
import com.ace.app.model.PaperId;

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
public class ExamCandidateDTO extends BaseCandidateDTO {

	private Long examId = 0L;

	private String currentPaperName = "";
	private Integer currentPaperQuestionNumber = 0;
	private Short currentPaperQuestionOptionIndex = null;

	private String currentQuestionStr = "";
	private List<String> currentOptions = new ArrayList<>();
	private Short currentAnswerIndex = null;

	private List<String> paperNames = new ArrayList<>();
	private Map<Integer, Boolean> currentPaperQuestionsAnswered = new LinkedHashMap<>();

	private String nextQuestionPath = null;
	private String prevQuestionPath = null;

	private Float timeUsed = 0.0f;

	public ExamCandidateDTO( Candidate candidate ) {
		examId = candidate.getExam().getExamId();
		field1 = candidate.getField1();
		field2 = candidate.getField2();
		// candidate.getPapers().forEach( paper -> {
		// 	paperNames.add( paper.getName() );
		// } );
		if ( candidate.getPapernames() == null ) {
			paperNames = new ArrayList<>();
		} else {
			paperNames = candidate.getPapernames();
		}
	}

	public ExamCandidateDTO( Candidate candidate, CandidateQuestionAnswerMapper currentMap ) {
		examId = candidate.getExam().getExamId();
		field1 = candidate.getField1();
		field2 = candidate.getField2();

		currentPaperName = currentMap.getPaperName();
		currentPaperQuestionNumber = currentMap.getCandidateQuestionNumber();
		currentPaperQuestionOptionIndex = currentMap.getAnswerIndex();

		currentQuestionStr = currentMap.getQuestion().getQuestion();
		currentOptions = currentMap.getQuestion().getOptions();
		currentAnswerIndex = currentMap.getAnswerIndex();

		paperNames = candidate.getPapernames();
		currentPaperQuestionsAnswered = new LinkedHashMap<>();
		candidate.getAnswerMapper().forEach( map -> {
			if ( currentPaperName.equals( map.getPaperName() ) ) {
				currentPaperQuestionsAnswered.put( map.getCandidateQuestionNumber(), map.getAnswerIndex() != null );
			}
		} );

		// TODO Validate the paths
		int currentPaperIndex = paperNames.indexOf( currentPaperName );
		Paper currPaper = currentMap.getQuestion().getPaper();
		if ( currentPaperIndex == 0 && currentPaperQuestionNumber == 1 ) { // First paper and first question
			prevQuestionPath = null;
		} else if ( currentPaperIndex > 0 && currentPaperQuestionNumber == 1 ) { // Not first paper and first question
			String prevPapername = paperNames.get( currentPaperIndex - 1 );
			for ( var map : candidate.getAnswerMapper() ) {
				if ( map.getQuestion().getPaper().getName().equals( prevPapername ) ) {
					prevQuestionPath = prevPapername + "/" + map.getQuestion().getPaper().getQuestionsPerCandidate();
					break;
				}
			}
			// prevQuestionPath = paperNames.get( currentPaperIndex - 1 ) + "/" + candidate.getPapers().get( currentPaperIndex - 1 ).getQuestionsPerCandidate();
		} else { // Any paper and not first question
			prevQuestionPath = currentPaperName + "/" + ( currentPaperQuestionNumber - 1 );
		}
		if ( currentPaperIndex == paperNames.size() - 1 && currentPaperQuestionNumber == currPaper.getQuestionsPerCandidate() ) { // Last paper and last question
			nextQuestionPath = "submit";
		} else if ( currentPaperIndex < paperNames.size() - 1 && currentPaperQuestionNumber == currPaper.getQuestionsPerCandidate() ) { // Not last paper and last question 
			nextQuestionPath = paperNames.get( currentPaperIndex + 1 ) + "/1";
		} else { // Any paper and not last question
			nextQuestionPath = currentPaperName + "/" + ( currentPaperQuestionNumber + 1 );
		}

		timeUsed = candidate.getTimeUsed();
	}

	public CandidateId getCandidateId( Exam exam ) {
		return new CandidateId( exam, field1, field2 );
	}

	public PaperId getPaperId( Exam exam ) {
		return new PaperId( exam, currentPaperName );
	}

	public void setCurrentAsPrevQuestionPath() {
		prevQuestionPath = currentPaperName + "/" + currentPaperQuestionNumber;
	}
}
