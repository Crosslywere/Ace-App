package com.ace.app.model;

import java.io.Serializable;

import com.ace.app.entity.Exam;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 20-May-2024
 */
@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PaperId implements Serializable {
	@ManyToOne( cascade = CascadeType.ALL )
	@JoinColumn( name = "exam_id" )
	private Exam exam;

	private String name;

}
