package com.ace.app.entity;

import com.ace.app.model.CandidateField;

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

	private Boolean serialNumber = true;

	private String serialNumberStr = "S/n";

	private String field1Str;

	private Boolean field2 = true;

	private String field2Str;

	private Boolean timeUsed = true;

	private String timeUsedStr = "Time Used(in seconds)";

	// Using byte to create numbers that range from -1 to 1
	// -1 representing Only the Question Number
	//  0 representing The Question and Number
	//  1 representing Only the Question
	private Byte questionAs = -1;

	// Same as questionAs but this time for answer indices
	private Byte answerAs = -1; 

	private Boolean allCandidates = true;

	private Boolean showScore = true;

	public ExportConfig( Exam exam ) {
		this.examId = exam.getExamId();
	}

	public String export( Exam exam ) {
		if ( filename ==  null ) {
			filename = exam.getTitle() + ".csv";
		}

		// Head of CSV
		boolean firstEntry = true;
		String head = "", body = "";
		if ( serialNumber ) {
			head = (serialNumberStr == null || serialNumberStr.isEmpty() ? "S/N" : serialNumberStr );
			firstEntry = false;
		}
		head += ( !firstEntry ? "," : "" ) + field1Str;
		if ( field2 && exam.getLoginField2() != CandidateField.None ) {
			head += "," + field2Str;
		}
		if ( timeUsed ) {
			head += "," + (timeUsedStr == null || timeUsedStr.isBlank() ? "Time Used(in seconds)" : timeUsedStr );
		}
		// Done this way because papers can have different papers per candidate 
		// also keeps things organized into organized columns in the .csv file
		for ( Paper paper : exam.getPapers() ) {
			head += "," + paper.getName();
			for ( int j = 0; j < paper.getQuestionsPerCandidate(); j++ ) {
				head += "," + paper.getName() + "Question,Answer";
			}
		}
		head += ",Score";
		// Body of CSV
		int c = 1;
		for ( Candidate candidate : exam.getCandidates() ) {
			firstEntry = true;
			if ( allCandidates || candidate.getHasLoggedIn() ) {
				int maxScore = 0;
				if ( serialNumber ) {
					body += "\n" + c++;
					firstEntry = false;
				}
				body += ( !firstEntry ? "," : "\n" ) + candidate.getField1();
				if ( field2 ) {
					body += "," + candidate.getField2();
				}
				if ( timeUsed ) {
					body += "," + candidate.getTimeUsed();
				}
				for ( Paper paper : exam.getPapers() ) {
					if ( !candidate.getPapers().contains( paper ) ) {
						body += "," + 0;
						for ( int i = 0; i < paper.getQuestionsPerCandidate(); i++ ) {
							body += ",,";
						}
					} else {
						body += "," + 1;
						maxScore += paper.getQuestionsPerCandidate();
						for ( CandidateQuestionAnswerMapper mapper : candidate.getAnswerMapper() ) {
							if ( mapper.getPaperName().equals( paper.getName() ) ) {
								if ( questionAs < 0 ) {
									body += "," + mapper.getQuestion().getNumber();
								} else if ( questionAs > 0 ) {
									body += ",\"" + mapper.getQuestion().getQuestion() + "\"";
								} else {
									body += ",\"" + mapper.getQuestion().getNumber() + ". " + mapper.getQuestion().getQuestion() + "\"";
								}
								if ( answerAs < 0 ) {
									body += "," +  ( char )( mapper.getAnswerIndex() + 'A' );
								} else if ( answerAs > 0 ) {
									body += ",\"" + mapper.getQuestion().getOptions().get( mapper.getAnswerIndex() ) + "\"" ; 
								} else {
									body += ",\"" + ( char )( mapper.getAnswerIndex() + 'A' ) + ". " + mapper.getQuestion().getOptions().get( mapper.getAnswerIndex() ) + "\"";
								}
							}
						}
					}
				}
				if ( showScore ) {
					body += "," + candidate.getScore() + "/" + maxScore;
				}
			}
		}
		return head + body;
	}
}
