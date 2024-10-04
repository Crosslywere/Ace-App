package com.ace.app.dto;

import java.sql.Date;

import com.ace.app.model.CandidateField;
import com.ace.app.model.ExamState;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Getter
@Setter
public abstract class BaseExamDTO {

	protected Long examId;
	protected String title;
	protected Date scheduledDate;
	protected String startTime;
	protected String endTime;
	protected Integer duration;
	protected ExamState state;
	protected Float cutOffMark;
	protected Boolean allowCutOffMark;
	protected Boolean showResult;
	protected CandidateField loginField1;
	protected String loginField1Desc;
	protected CandidateField loginField2;
	protected String loginField2Desc;
	// TODO Remove papersPerCandidate variable (Candidates upload & paper upload will determine paper count)
	protected Integer papersPerCandidate;

}
