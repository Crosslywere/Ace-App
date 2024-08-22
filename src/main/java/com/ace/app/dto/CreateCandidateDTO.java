package com.ace.app.dto;

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
public class CreateCandidateDTO extends BaseCandidateDTO {
	private String email;
	private String phoneNumber;
	private Boolean notified;
}
