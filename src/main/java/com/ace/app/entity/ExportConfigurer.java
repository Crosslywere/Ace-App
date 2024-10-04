package com.ace.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ExportConfigurer {
	
	@Id
	private final Integer configId = 1;

	private Boolean field1 = true;

	private Boolean field2 = false;

	private Boolean names = true;

	private Boolean email = true;

	private Boolean phone = true;

	private Boolean state = false;

	private Boolean timeUsed = true;

	private Boolean paperScores = true;

	private Boolean exportAllCandidates = true;

	public String generateCSV( Exam exam ) {
		String head = "", body = "";
		// Head
		head += exam.getLoginField1Desc() + ",";
		head += exam.getLoginField2Desc() + ",";
		head += "Email,";
		head += "Phone,";
		head += "State,";
		head += "Firstname";
		head += "Lastname,";
		head += "Othername,";
		head += "TimeUsed,";
		for ( var paper : exam.getPapers() ) {
			head += paper.getName() + "Score,";
		}
		head += "TotalScore";
		exam.getCandidates().sort( Candidate::compareScore );
		// Body
		for ( var candidate : exam.getCandidates() ) {
			body += candidate.getField1() + ",";
			body += candidate.getField2() + ",";
			body += candidate.getEmail() + ",";
			body += candidate.getPhoneNumber() + ",";
			body += candidate.getState() + ",";
			body += candidate.getFirstname() + ",";
			body += candidate.getLastname() + ",";
			body += candidate.getOthername() + ",";
			body += candidate.getTimeUsed() + ",";
			for ( var paper : exam.getPapers() ) {
				if ( candidate.getPapernames().contains( paper.getName() ) ) {
					body += Candidate.score( candidate, paper.getName() ) + ",";
				} else {
					body += "-,";
				}
			}
			body += candidate.getScore() + "\n";
		}
		return head + "\n" + body;
	}

}
