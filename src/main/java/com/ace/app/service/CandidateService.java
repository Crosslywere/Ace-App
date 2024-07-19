package com.ace.app.service;

import com.ace.app.entity.Candidate;
import com.ace.app.model.CandidateException;
import com.ace.app.dto.ExamCandidateDTO;
import com.ace.app.dto.RegisterCandidateDTO;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
public interface CandidateService {

	/**
	 * 
	 * @param candidateDTO
	 * @return
	 * @throws CustomException
	 */
	Candidate putCandidate( RegisterCandidateDTO candidateDTO ) throws CandidateException;

	/**
	 * 
	 * @param candidateDTO
	 * @return
	 * @throws CustomException
	 */
	Candidate loginCandidate( RegisterCandidateDTO candidateDTO ) throws CandidateException;

	/**
	 * 
	 * @param candidateDTO
	 * @return
	 * @throws CandidateException
	 */
	ExamCandidateDTO submitQuestion( ExamCandidateDTO candidateDTO, String paperName, Integer number ) throws CandidateException;
}
