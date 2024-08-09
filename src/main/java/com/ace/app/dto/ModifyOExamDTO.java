package com.ace.app.dto;

import java.util.ArrayList;
import java.util.List;

import com.ace.app.entity.Exam;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Getter
@Setter
@NoArgsConstructor
public class ModifyOExamDTO extends BaseExamDTO {

	private Boolean registrationLocked;
	private List<ModifyOCandidateDTO> candidates  = new ArrayList<>();

	public ModifyOExamDTO( Exam exam ) {
		super();
		super.examId = exam.getExamId();
		super.title = exam.getTitle();
		super.endTime = exam.getEndTime().toString().replaceAll( ":\\d+$", "" );
		super.cutOffMark = exam.getCutOffMark();
		super.allowCutOffMark = exam.getAllowCutOffMark();
		super.showResult = exam.getShowResult();
		super.loginField1 = exam.getLoginField1();
		super.loginField1Desc = exam.getLoginField1Desc();
		super.loginField2 = exam.getLoginField2();
		super.loginField2Desc = exam.getLoginField2Desc();
		this.registrationLocked = exam.getRegistrationLocked();
		this.candidates = new ArrayList<>();
		exam.getCandidates().forEach( candidate -> {
			candidates.add( new ModifyOCandidateDTO( candidate ) );
		} );
	}
}
