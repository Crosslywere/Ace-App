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
public abstract class BasePaperDTO {

	protected String name;
	protected Integer questionsPerCandidate;
	protected Boolean manditory = false;
}
