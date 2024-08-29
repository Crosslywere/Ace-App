package com.ace.app.dto;

import com.ace.app.entity.Candidate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExamCandidateRateDTO extends BaseCandidateDTO {
	
	private Long examId;
	private Byte rating = 5;
	private String comment = "";

	public ExamCandidateRateDTO( Candidate candidate ) {
		examId = candidate.getExam().getExamId();
		field1 = candidate.getField1();
		field2 = candidate.getField2();
	}
}
