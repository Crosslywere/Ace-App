package com.ace.app.model;

import lombok.Getter;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Getter
public class CandidateException extends RuntimeException {

	private CandidateExceptionRedirect redirect;

	public CandidateException( String message, CandidateExceptionRedirect redirect ) {
		super( message );
		this.redirect = redirect;
	}
}
