package com.ace.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ExportConfig {
	
	@Id
	private final Byte configId = 1;
	
	@Transient
	private Long examId;

	@Transient
	private String filename;

	private Boolean field2 = true;

	private Boolean timeUsed = true;

	public ExportConfig( Exam exam ) {
		this.examId = exam.getExamId();
		this.filename = exam.getTitle() + ".csv";
	}

	public String export( Exam exam ) {
		if ( filename ==  null ) {
			filename = exam.getTitle() + ".csv";
		}
		String head = "", body = "";
		head = "S/N";
		head += "," + exam.getLoginField1Desc();
		head += "," + exam.getLoginField2Desc();
		head += ",TimeUsed";
		for ( Paper paper : exam.getPapers() ) {
			head += "," + paper.getName();
			for ( int j = 0; j < paper.getQuestionsPerCandidate(); j++ ) {
				head += "," + paper.getName() + "Question,Answer";
			}
		}
		head += ",Score";
		int c = 1;
		for ( Candidate candidate : exam.getCandidates() ) {
			if ( candidate.getHasLoggedIn() || candidate.getSubmitted() ) {
				body += "\n" + c++;
				body += "," + candidate.getField1();
				body += "," + candidate.getField2();
				body += "," + candidate.getTimeUsed() + 's';
				for ( Paper paper : exam.getPapers() ) {
					if ( !candidate.getPapers().contains( paper ) ) {
						body += "," + 0;
						for ( int i = 0; i < paper.getQuestionsPerCandidate(); i++ ) {
							body += ",,";
						}
					} else {
						body += "," + 1;
						for ( CandidateQuestionAnswerMapper mapper : candidate.getAnswerMapper() ) {
							if ( mapper.getPaperName().equals( paper.getName() ) ) {
								body += ",\"" + mapper.getQuestion().getNumber() + ". " + mapper.getQuestion().getQuestion() + "\"";
								body += ",\"" + ( char )( mapper.getAnswerIndex() + 'A' ) + ". " + mapper.getQuestion().getOptions().get( mapper.getAnswerIndex() ) + "\"";
							}
						}
					}
				}
				body += "," + candidate.getScore();
			}
		}
		return head + body;
	}
}
