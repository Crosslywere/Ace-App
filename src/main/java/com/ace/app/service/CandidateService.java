package com.ace.app.service;

import com.ace.app.entity.Candidate;
import com.ace.app.model.CandidateException;
import com.ace.app.dto.ExamCandidateDTO;
import com.ace.app.dto.UnverifiedCandidateDTO;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
public interface CandidateService {

	/**
	 * 
	 * @param candidate
	 */
	void update( Candidate candidate );

	/***
	 * 
	 * @param candidateDTO
	 * @return
	 * @throws CandidateException
	 */
	Candidate login( UnverifiedCandidateDTO candidateDTO ) throws CandidateException;

	/**
	 * 
	 * @param candidateDTO
	 * @return
	 * @throws CustomException
	 */
	Candidate putCandidate( UnverifiedCandidateDTO candidateDTO ) throws CandidateException;

	/**
	 * 
	 * @param candidateDTO
	 * @return
	 * @throws CustomException
	 */
	Candidate loginCandidate( UnverifiedCandidateDTO candidateDTO ) throws CandidateException;

	/**
	 * 
	 * @param candidateDTO
	 * @return
	 * @throws CandidateException
	 */
	ExamCandidateDTO submitQuestion( ExamCandidateDTO candidateDTO, String paperName, Integer number ) throws CandidateException;

	/**
	 * 
	 * @param candidateDTO
	 * @throws CandidateException
	 */
	void answerQuestion( ExamCandidateDTO candidateDTO ) throws CandidateException;

	/**
	 * 
	 * @param candidateDTO
	 * @param paperName
	 * @param questionNumber
	 * @return
	 * @throws CandidateException
	 */
	ExamCandidateDTO getExamCandidateQuestion( ExamCandidateDTO candidateDTO, String paperName, Integer questionNumber ) throws CandidateException;

	/**
	 * 
	 * @param candidateDTO
	 * @throws CandidateException
	 */
	Candidate submitExam( ExamCandidateDTO candidateDTO ) throws CandidateException;
}
