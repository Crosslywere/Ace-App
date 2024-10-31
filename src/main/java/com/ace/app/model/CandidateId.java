package com.ace.app.model;

import java.io.Serializable;

import com.ace.app.entity.Exam;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CandidateId implements Serializable {

	@ManyToOne
	private Exam exam;

	private String field1;

	private String field2;
}
