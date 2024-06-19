package com.ace.app.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 19-June-2024
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginExamCandidateDTO extends BaseCandidateDTO {

	private Long examId;
	private List<String> papers = new ArrayList<>();
}
