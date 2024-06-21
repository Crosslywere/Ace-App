package com.ace.app.dto;

import com.ace.app.entity.Candidate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 20-June-2024
 */
@Getter
@Setter
@NoArgsConstructor
public class ModifyOCandidateDTO extends BaseCandidateDTO {

	Boolean hasLoggedIn;
	Boolean oldHasLoggedIn;
	Boolean submitted;

	public ModifyOCandidateDTO( Candidate candidate ) {
		super();
		super.field1 = candidate.getField1();
		super.field2 = candidate.getField2();
		this.hasLoggedIn = this.oldHasLoggedIn = candidate.getHasLoggedIn();
		this.submitted = candidate.getSubmitted();
	}
}
