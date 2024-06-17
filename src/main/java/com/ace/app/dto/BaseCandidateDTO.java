package com.ace.app.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode( exclude = "field2" )
public abstract class BaseCandidateDTO {

	protected String field1;
	protected String field2;
}
