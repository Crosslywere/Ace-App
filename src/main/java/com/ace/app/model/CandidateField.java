package com.ace.app.model;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
public enum CandidateField {
	// When no data is expected
	None( "" ),
	// When expected data is in the form of plane text
	Text( "text" ),
	// When expected data is in the form of an email
	Email( "email" ),
	// When expected data is in the form of a phone number
	Telephone( "tel" ),
	// When expected data is in the form of a numbers
	Number( "number" ),
	// When expected data is meant to be hidden as a password
	Password( "password" );

	private final String type;

	private CandidateField( String type ) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}
}
