package com.ace.app.dto;

import com.ace.app.entity.Candidate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 19-June-2024
 */
@Getter
@Setter
@NoArgsConstructor
public class ModifyOCandidateDTO extends BaseCandidateDTO {

	Boolean hasLoggedIn;
	Boolean submitted;

	public ModifyOCandidateDTO( Candidate candidate ) {
		super();
		super.field1 = candidate.getField1();
		super.field2 = candidate.getField2();
		this.hasLoggedIn = candidate.getHasLoggedIn();
		this.submitted = candidate.getSubmitted();
	}
}
