package com.ace.app.model;

import lombok.Getter;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
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

	public CandidateException( CandidateExceptionType type, Object object ) {

	}

	public CandidateException( CandidateExceptionType type ) {
		
	}
}
