package com.ace.app.dto;

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
public class ModifySCandidateDTO extends BaseCandidateDTO {

	public ModifySCandidateDTO( Candidate candidate ) {
		super();
		super.field1 = candidate.getField1();
		super.field2 = candidate.getField2();
	}

	public ModifySCandidateDTO( CreateCandidateDTO candidateDTO ) {
		super();
		super.field1 = candidateDTO.getField1();
		super.field2 = candidateDTO.getField2();
	}
}
