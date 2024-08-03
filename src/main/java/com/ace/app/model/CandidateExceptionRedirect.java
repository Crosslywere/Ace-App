package com.ace.app.model;

import lombok.Getter;

@Getter
public enum CandidateExceptionRedirect {
	EXAM_SELECT( "candidate/exam-select" ),
	LOGIN_PAGE( "candidate/exam-login" ),
	PAPER_SELECT( "candidate/exam-papers" ),
	ERROR( "candidate/error" );

	private final String path;

	private CandidateExceptionRedirect( String path ) {
		this.path = path;
	}
}
