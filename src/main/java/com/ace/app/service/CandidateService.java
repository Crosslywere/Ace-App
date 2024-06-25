package com.ace.app.service;

import com.ace.app.dto.LoginExamCandidateDTO;

/**
 * @author Ogboru Jude
 * @version 22-June-2024
 */
public interface CandidateService {

	/**
	 * @param candidateDTO
	 * @return
	 */
	LoginExamCandidateDTO login( LoginExamCandidateDTO candidateDTO );
}
