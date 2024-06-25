package com.ace.app.dto;

import java.util.ArrayList;
import java.util.List;

import com.ace.app.entity.Candidate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 22-June-2024
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginExamCandidateDTO extends BaseCandidateDTO {

	private Long examId;
	private List<String> paperNames = new ArrayList<>();

	public LoginExamCandidateDTO( Candidate candidate ) {
		super();
		super.field1 = candidate.getField1();
		super.field2 = candidate.getField2();
		examId = candidate.getExam().getExamId();
		paperNames = new ArrayList<>();
		candidate.getPapers().forEach( paper -> {
			paperNames.add( paper.getName() );
		} );
	}
}
