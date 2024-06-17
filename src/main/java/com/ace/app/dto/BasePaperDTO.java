package com.ace.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 21-May-2024
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class BasePaperDTO {

	protected String name;
	protected Integer questionsPerCandidate;
	protected Boolean manditory = false;
}
