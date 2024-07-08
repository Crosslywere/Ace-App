package com.ace.app.model;

import lombok.Getter;

/**
 * @author Ogboru Jude
 * @version 01-July-2024
 */
@Getter
public class CandidateException extends Exception {
	
	private CandidateExceptionType type;
	
	private Object object = null;

	public CandidateException( String message, CandidateExceptionType type, Object object ) {
		super( message );
		this.type = type;
		this.object = object;
	}
}
