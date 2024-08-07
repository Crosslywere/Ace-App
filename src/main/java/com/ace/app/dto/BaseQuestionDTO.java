package com.ace.app.dto;

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
public abstract class BaseQuestionDTO {

	protected Integer number;
	protected String question;
	protected List<String> options;

	public void appendToQuestion( String line ) {
		this.question += line;
	}
}
