package com.ace.app.dto;

import com.ace.app.entity.Exam;

/**
 * @author Ogboru Jude
 * @version 29-May-2024
 */
public class ModifyOExamDTO  extends BaseExamDTO {

	public ModifyOExamDTO( Exam exam ) {
		super();
		super.examId = exam.getExamId();
		super.endTime = exam.getEndTime().toString().replaceAll( ":\\d+$", "" );
		super.cutOffMark = exam.getCutOffMark();
		super.allowCutOffMark = exam.getAllowCutOffMark();
		super.showResult = exam.getShowResult();
		super.loginField1 = exam.getLoginField1();
		super.loginField1Desc = exam.getLoginField1Desc();
		super.loginField2 = exam.getLoginField2();
		super.loginField2Desc = exam.getLoginField2Desc();
	}
}
