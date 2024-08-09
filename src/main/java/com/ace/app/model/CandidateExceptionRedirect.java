package com.ace.app.model;

import lombok.Getter;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Getter
public enum CandidateExceptionRedirect {
	EXAM_SELECT( "candidate/exam-select" ),
	LOGIN_PAGE( "candidate/exam-login" ),
	PAPER_SELECT( "candidate/exam-papers" ),
	SUBMITTED( "candidate/exam-submitted" ),
	ERROR( "error" );

	private final String path;

	private CandidateExceptionRedirect( String path ) {
		this.path = path;
	}
}
