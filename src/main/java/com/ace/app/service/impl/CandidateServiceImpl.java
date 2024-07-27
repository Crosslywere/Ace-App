package com.ace.app.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ace.app.dto.ExamCandidateDTO;
import com.ace.app.dto.RegisterCandidateDTO;
import com.ace.app.entity.Candidate;
import com.ace.app.entity.CandidateQuestionAnswerMapper;
import com.ace.app.entity.Exam;
import com.ace.app.entity.Paper;
import com.ace.app.entity.Question;
import com.ace.app.model.CandidateException;
import com.ace.app.model.CandidateExceptionType;
import com.ace.app.model.CandidateId;
import com.ace.app.model.CandidateQuestionAnswerMapperId;
import com.ace.app.model.ExamState;
import com.ace.app.model.PaperId;
import com.ace.app.repository.CandidateQuestionAnswerMapperRepository;
import com.ace.app.repository.CandidateRepository;
import com.ace.app.repository.ExamRepository;
import com.ace.app.repository.PaperRepository;
import com.ace.app.service.CandidateService;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Service
@SuppressWarnings( "unused" )
public class CandidateServiceImpl implements CandidateService {
	
	@Autowired
	private ExamRepository examRepository;

	@Autowired
	private CandidateRepository candidateRepository;

	@Autowired
	private PaperRepository paperRepository;

	@Autowired
	private CandidateQuestionAnswerMapperRepository candidateQuestionAnswerMapperRepository;

	@Override
	public Candidate putCandidate( RegisterCandidateDTO candidateDTO ) throws CandidateException {
		if ( candidateDTO == null || candidateDTO.getExamId() == null ) {
			// Return exam select page
			throw new CandidateException( "Invalid exam selected", CandidateExceptionType.INVALID_EXAM, null );
		}
		Exam exam = examRepository.findById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null || exam.getState() != ExamState.Ongoing ) {
			// Return exam select page
			throw new CandidateException( "Invalid exam selected (may not be ongoing)", CandidateExceptionType.INVALID_EXAM, null );
		}
		Candidate tempCandidate = new Candidate( candidateDTO, exam );
		if ( exam.getRegistrationLocked() ) {
			Candidate candidate = candidateRepository.findById( tempCandidate.getCandidateId() ).orElse( null );
			if ( candidate != null && !candidate.getHasLoggedIn() ) {
				return candidate;
			} else if ( candidate != null && candidate.getHasLoggedIn() ) {
				// Return candidate login page
				throw new CandidateException( "Already logged in", CandidateExceptionType.INVALID_CANDIDATE, candidateDTO );
			} else {
				// Return candidate login page
				throw new CandidateException( "Invalid Credentials", CandidateExceptionType.INVALID_CREDENTIALS, candidateDTO );
			}
		} else {
			Candidate candidate = candidateRepository.findById( tempCandidate.getCandidateId() ).orElse( null );
			if ( candidate != null && !candidate.getHasLoggedIn() ) {
				return candidate;
			} else if ( candidate != null && candidate.getHasLoggedIn() ) {
				// Return candidate login page
				throw new CandidateException( "Already logged in", CandidateExceptionType.INVALID_CANDIDATE, candidateDTO );
			} else {
				return candidateRepository.save( tempCandidate );
			}
		}
	}

	@Override
	public Candidate loginCandidate( RegisterCandidateDTO candidateDTO ) throws CandidateException {
		if ( candidateDTO == null || candidateDTO.getExamId() == null ) {
			// Return exam select page
			throw new CandidateException( "Invalid exam selected", CandidateExceptionType.INVALID_EXAM, null );
		}
		Exam exam = examRepository.findById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null || exam.getState() != ExamState.Ongoing ) {
			// Return exam select page
			throw new CandidateException( "Invalid exam selected", CandidateExceptionType.INVALID_EXAM, null );
		}
		Candidate candidate = candidateRepository.findById( new Candidate( candidateDTO, exam ).getCandidateId() ).orElse( null );
		if ( candidate == null ) {
			// Return candidate login page
			throw new CandidateException( "Unregistered candidate", CandidateExceptionType.INVALID_CREDENTIALS, candidateDTO );
		}
		if ( candidate.getPapers().size() != exam.getPapersPerCandidate() ) {
			candidate.getPapers().clear();
			candidateDTO.getPaperNames().forEach( paperName -> {
				Paper paper = paperRepository.findById( new PaperId( exam, paperName ) ).orElse( null );
				if ( paper != null ) {
					candidate.getPapers().add( paper );
				}
			} );
			if ( candidate.getPapers().size() != exam.getPapersPerCandidate() ) {
				throw new CandidateException( "Invalid number of papers selected", CandidateExceptionType.INVALID_PAPER_COUNT, candidateDTO );
			} else {
				for ( Paper paper : exam.getPapers() ) {
					if ( paper.getManditory() ) {
						if ( !candidate.getPapers().contains( paper ) ) {
							// Return exam paper select page
							throw new CandidateException( "Please select manditory paper(" + paper.getName() + ")", CandidateExceptionType.INVALID_PAPER_SELECTED, candidateDTO );
						}
					}
				}
				candidate.setHasLoggedIn( true );
				candidate.getPapers().forEach( paper -> {
					appendQuestions( candidate, paper );
				} );
				return candidateRepository.save( candidate );
			}
		} else {
			for ( Paper paper : exam.getPapers() ) {
				if ( paper.getManditory() ) {
					if ( !candidate.getPapers().contains( paper ) ) {
						// Return exam paper select page
						throw new CandidateException( "Please select manditory paper(" + paper.getName() + ")", CandidateExceptionType.INVALID_PAPER_SELECTED, candidateDTO );
					}
				}
			}
			candidate.setHasLoggedIn( true );
			candidate.getPapers().forEach( paper -> {
				appendQuestions( candidate, paper );
			} );
			return candidateRepository.save( candidate );
		}
	}

	public ExamCandidateDTO submitQuestion( ExamCandidateDTO candidateDTO, String paperName, Integer questionNumber ) throws CandidateException {
		if ( candidateDTO == null ) {
			throw new CandidateException( "Invalid candidate", CandidateExceptionType.INVALID_CANDIDATE, null );
		}
		Exam exam = examRepository.findById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null || exam.getState() != ExamState.Ongoing ) {
			throw new CandidateException( "Invalid exam", CandidateExceptionType.INVALID_EXAM, null );
		}
		Candidate candidate = candidateRepository.findById( candidateDTO.getCandidateId( exam ) ).orElse( null );
		if ( candidate == null ) {
			// TODO throw exception
			throw new CandidateException( "Invalid candidate", CandidateExceptionType.INVALID_CANDIDATE, null );
		}
		if ( candidateDTO.getTimeUsed() != null ) {
			candidate.setTimeUsed( candidateDTO.getTimeUsed() );
			candidate.setHasLoggedIn( true );
			if ( candidate.getTimeUsed() >= exam.getDuration() ) {
				candidate.setSubmitted( true );
			}
			candidateRepository.save( candidate );
			Paper paper = paperRepository.findById( new PaperId( exam, candidateDTO.getCurrentPaperName() ) ).orElse( null );
			if ( paper != null ) {
				CandidateQuestionAnswerMapperId mapId = new CandidateQuestionAnswerMapperId( candidateDTO.getCurrentPaperQuestionNumber(), candidate, paper );
				CandidateQuestionAnswerMapper answeredMap = candidateQuestionAnswerMapperRepository.findById( mapId ).orElse( null );
				if ( answeredMap != null ) {
					// TODO Fix saving the answer
					answeredMap.setAnswerIndex( candidateDTO.getCurrentAnswerIndex() );
					// int i = candidate.getAnswerMapper().indexOf( answeredMap );
					// candidate.getAnswerMapper().set( i, answeredMap );
					candidateQuestionAnswerMapperRepository.save( answeredMap );
					// candidateRepository.save( candidate );
				}
			}
		}
		if ( candidate.getTimeUsed() >= exam.getDuration() * 60 ) {
			// Return submitted page
			throw new CandidateException( "Time concluded", CandidateExceptionType.TIME_CONCLUDED, null );
		}
		PaperId paperId = new PaperId( exam, paperName );
		Paper paper = paperRepository.findById( paperId ).orElse( null );
		if ( paper == null ) {
			// Paper doesn't exist
			throw new CandidateException( "Invalid paper", CandidateExceptionType.INVALID_PAPER_SELECTED, null );
		}
		CandidateQuestionAnswerMapperId mapId = new CandidateQuestionAnswerMapperId( questionNumber, candidate, paper );
		CandidateQuestionAnswerMapper map = candidateQuestionAnswerMapperRepository.findById( mapId ).orElse( null );
		if ( map == null ) {
			// Return error page
			throw new CandidateException( "Invalid question", CandidateExceptionType.INVALID_QUESTION, null );
		}
		return new ExamCandidateDTO( candidate, map );
	}

	private void appendQuestions( Candidate candidate, Paper paper ) {
		Random rand = new Random( candidate.getCandidateId().hashCode() + paper.getPaperId().hashCode() );
		Collections.shuffle( paper.getQuestions(), rand );
		if ( candidate.getAnswerMapper() == null ) {
			candidate.setAnswerMapper( new ArrayList<>() );
		}
		for ( int i = 0; i < paper.getQuestionsPerCandidate(); i++ ) {
			candidate.getAnswerMapper().add( new CandidateQuestionAnswerMapper(  i + 1, candidate, paper.getQuestions().get( i ) ) );
		}
	}
}
