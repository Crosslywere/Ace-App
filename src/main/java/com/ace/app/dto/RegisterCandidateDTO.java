package com.ace.app.dto;

import java.util.ArrayList;
import java.util.List;

import com.ace.app.entity.Candidate;

import io.micrometer.common.lang.NonNull;
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
public class RegisterCandidateDTO extends BaseCandidateDTO {

	private Long examId;
	private List<String> paperNames = new ArrayList<>();

	public RegisterCandidateDTO( @NonNull Candidate candidate ) {
		super();
		super.field1 = candidate.getField1();
		super.field2 = candidate.getField2();
		this.examId = candidate.getExam().getExamId();
		this.paperNames = new ArrayList<>();
		candidate.getPapers().forEach( paper -> {
			paperNames.add( paper.getName() );
		} );
	}
}
