package com.ace.app.dto;

import java.util.ArrayList;
import java.util.List;

import com.ace.app.entity.Candidate;

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

	private Long examId;

	private Float timeUsed;

	private String paperName;

	private Integer questionNumber;

	private String question;

	private List<ExamPaperDTO> papers = new ArrayList<>();
	
	public ExamCandidateDTO( Candidate candidate ) {
		super();
		super.field1 = candidate.getField1();
		super.field2 = candidate.getField2();
		this.examId = candidate.getExam().getExamId();
		this.timeUsed = candidate.getTimeUsed();
		this.papers = new ArrayList<>();
		candidate.getPapers().forEach( paper -> {
			papers.add( new ExamPaperDTO( paper, candidate ) );
		} );
	}

	public ExamCandidateDTO( Candidate candidate, String paperName, Integer questionNumber ) {
		super();
		super.field1 = candidate.getField1();
		super.field2 = candidate.getField2();
		this.examId = candidate.getExam().getExamId();
		this.timeUsed = candidate.getTimeUsed();
		this.paperName = paperName;
		this.questionNumber = questionNumber;
		this.papers = new ArrayList<>();
		candidate.getPapers().forEach( paper -> {
			papers.add( new ExamPaperDTO( paper, candidate ) );
		} );
	}
}
