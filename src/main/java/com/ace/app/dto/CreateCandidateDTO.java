package com.ace.app.dto;

import java.util.ArrayList;
import java.util.List;

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
	protected String email;
	protected String phoneNumber;
	protected Boolean notified;
	protected String firstname;
	protected String othername;
	protected String lastname;
	protected String state;
	protected List<String> papernames = new ArrayList<>();
}
