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

		paperNames = new ArrayList<>();
		candidate.getPapers().forEach( paper -> {
			paperNames.add( paper.getName() );
		} );
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

		paperNames = new ArrayList<>();
		candidate.getPapers().forEach( paper -> {
			paperNames.add( paper.getName() );
		} );
		currentPaperQuestionsAnswered = new LinkedHashMap<>();
		candidate.getAnswerMapper().forEach( map -> {
			if ( currentPaperName.equals( map.getPaperName() ) ) {
				currentPaperQuestionsAnswered.put( map.getCandidateQuestionNumber(), map.getAnswerIndex() != null );
			}
		} );

		// TODO Validate the paths
		int currentPaperIndex = paperNames.indexOf( currentPaperName );
		Paper currentPaper = candidate.getPapers().get( currentPaperIndex );
		if ( currentPaperIndex == 0 && currentPaperQuestionNumber == 1 ) {
			prevQuestionPath = null;
		} else if ( currentPaperIndex > 0 && currentPaperQuestionNumber == 1 ) {
			prevQuestionPath = paperNames.get( currentPaperIndex - 1 ) + "/" + candidate.getPapers().get( currentPaperIndex - 1 ).getQuestionsPerCandidate();
		} else {
			prevQuestionPath = currentPaperName + "/" + ( currentPaperQuestionNumber - 1 );
		}
		if ( currentPaperIndex == paperNames.size() - 1 && currentPaperQuestionNumber == currentPaper.getQuestionsPerCandidate() ) {
			nextQuestionPath = "submit";
		} else if ( currentPaperIndex < paperNames.size() - 1 && currentPaperQuestionNumber == currentPaper.getQuestionsPerCandidate() ) {
			nextQuestionPath = paperNames.get( currentPaperIndex + 1 ) + "/1";
		} else {
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
