package com.ace.app.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ace.app.dto.ExamCandidateDTO;
import com.ace.app.dto.UnverifiedCandidateDTO;
import com.ace.app.entity.Candidate;
import com.ace.app.entity.CandidateQuestionAnswerMapper;
import com.ace.app.entity.Exam;
import com.ace.app.entity.Paper;
import com.ace.app.model.CandidateException;
import com.ace.app.model.CandidateExceptionRedirect;
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
	public void update( Candidate candidate ) {
		if ( candidate == null ) {
			return;
		}
		candidateRepository.findById( candidate.getCandidateId() ).ifPresent( c -> candidateRepository.save( candidate ) );
	}

	// Logs in a candidate if they are valid else throws an error and returns the user back to the login page
	@Override
	public Candidate login( UnverifiedCandidateDTO candidateDTO ) throws CandidateException {
		if ( candidateDTO == null || candidateDTO.getExamId() == null ) {
			throw new CandidateException( "Unable to get candidate", CandidateExceptionRedirect.EXAM_SELECT );
		}
		Exam exam = examRepository.findById( candidateDTO.getExamId() )
		.orElseThrow( () -> new CandidateException( "Invalid exam selected", CandidateExceptionRedirect.EXAM_SELECT ) );
		if ( exam.getState() != ExamState.Ongoing ) {
			throw new CandidateException( "Invalid exam selected. Exam no longer holding.", CandidateExceptionRedirect.EXAM_SELECT );
		}
		var candidate = candidateRepository.findById( candidateDTO, exam )
		.orElseThrow( () -> new CandidateException( "Invalid credentials", CandidateExceptionRedirect.LOGIN_PAGE ) );
		if ( candidate.getSubmitted() ) {
			throw new CandidateException( "Already concluded", CandidateExceptionRedirect.LOGIN_PAGE );
		}
		if ( candidate.getHasLoggedIn() ) {
			throw new CandidateException( "Already logged in", CandidateExceptionRedirect.LOGIN_PAGE );
		}
		candidate.setHasLoggedIn( true );
		candidate.getPapernames().forEach( papername -> {
			var paper = paperRepository.findById( papername, exam ).orElse( null );
			if ( paper == null ) {
				candidate.getPapernames().remove( papername );
			} else {
				appendQuestions( candidate, paper );
			}
		} );
		// candidate.getPapers().forEach( paper -> appendQuestions( candidate, paper ) );
		return candidateRepository.save( candidate );
	}

	@Override
	public Candidate putCandidate( UnverifiedCandidateDTO candidateDTO ) throws CandidateException {
		if ( candidateDTO == null || candidateDTO.getExamId() == null ) {
			throw new CandidateException( "Unable to get candidate", CandidateExceptionRedirect.EXAM_SELECT );
		}
		Exam exam = examRepository.findById( candidateDTO.getExamId() )
		.orElseThrow( () -> new CandidateException( "Invalid exam selected", CandidateExceptionRedirect.EXAM_SELECT ) );
		if ( exam.getState() != ExamState.Ongoing ) {
			throw new CandidateException( "Invalid exam selected. Exam no longer holding.", CandidateExceptionRedirect.EXAM_SELECT );
		}
		Candidate tempCandidate = new Candidate( candidateDTO, exam );
		Candidate candidate = candidateRepository.findById( tempCandidate.getCandidateId() ).orElse( null );
		if ( candidate != null ) {
			if ( candidate.getHasLoggedIn() ) {
				throw new CandidateException( "Already logged in", CandidateExceptionRedirect.LOGIN_PAGE );
			}
			if ( candidate.getSubmitted() ) {
				throw new CandidateException( "Already submitted", CandidateExceptionRedirect.LOGIN_PAGE );
			}
		} else {
			throw new CandidateException( "Invalid credentials", CandidateExceptionRedirect.LOGIN_PAGE );
		}
		return candidate;
	}

	@Override
	public ExamCandidateDTO submitQuestion( ExamCandidateDTO candidateDTO, String paperName, Integer questionNumber ) throws CandidateException {
		if ( candidateDTO == null ) {
			throw new CandidateException( "Invalid candidate", CandidateExceptionRedirect.EXAM_SELECT );
		}
		Exam exam = examRepository.findById( candidateDTO.getExamId() )
		.orElseThrow( () -> new CandidateException( "Invalid exam selected", CandidateExceptionRedirect.EXAM_SELECT ) );
		if ( exam.getState() != ExamState.Ongoing ) {
			throw new CandidateException( "Exam is not ongoing", CandidateExceptionRedirect.EXAM_SELECT );
		}
		Candidate candidate = candidateRepository.findById( candidateDTO.getCandidateId( exam ) )
		.orElseThrow( () -> new CandidateException( "Invalid candidate", CandidateExceptionRedirect.EXAM_SELECT ) );
		if ( candidate.getSubmitted() ) {
			throw new CandidateException( "This candidate has already submitted", CandidateExceptionRedirect.SUBMITTED );
		}
		updateQuestion( candidateDTO, candidate, exam );
		return getQuestion( candidate, exam, paperName, questionNumber );
	}

	@Override
	public void answerQuestion( ExamCandidateDTO candidateDTO ) throws CandidateException {
		if ( candidateDTO == null ) {
			throw new CandidateException( "Invalid candidate", CandidateExceptionRedirect.EXAM_SELECT );
		}
		Exam exam = examRepository.findById( candidateDTO.getExamId() )
		.orElseThrow( () -> new CandidateException( "Invalid exam selected", CandidateExceptionRedirect.EXAM_SELECT ) );
		if ( exam.getState() != ExamState.Ongoing ) {
			throw new CandidateException( "Exam is not ongoing", CandidateExceptionRedirect.EXAM_SELECT );
		}
		Candidate candidate = candidateRepository.findById( candidateDTO.getCandidateId( exam ) )
		.orElseThrow( () -> new CandidateException( "Invalid candidate", CandidateExceptionRedirect.EXAM_SELECT ) );
		if ( candidate.getSubmitted() ) {
			return;
		}
		updateQuestion( candidateDTO, candidate, exam );
	}

	@Override
	public ExamCandidateDTO getExamCandidateQuestion( ExamCandidateDTO candidateDTO, String paperName, Integer questionNumber ) throws CandidateException {
		if ( candidateDTO == null ) {
			throw new CandidateException( "Invalid candidate", CandidateExceptionRedirect.EXAM_SELECT );
		}
		Exam exam = examRepository.findById( candidateDTO.getExamId() )
		.orElseThrow( () -> new CandidateException( "Invalid exam selected", CandidateExceptionRedirect.EXAM_SELECT ) );
		if ( exam.getState() != ExamState.Ongoing ) {
			throw new CandidateException( "Exam is not ongoing", CandidateExceptionRedirect.EXAM_SELECT );
		}
		Candidate candidate = candidateRepository.findById( candidateDTO.getCandidateId( exam ) )
		.orElseThrow( () -> new CandidateException( "Invalid candidate", CandidateExceptionRedirect.EXAM_SELECT ) );
		if ( candidate.getTimeUsed() >= exam.getDuration() * 60 ) {
			candidate.setSubmitted( true );
			if ( candidate.getScore() < 0 ) {
				Candidate.score( candidate );
			}
			candidateRepository.save( candidate );
			throw new CandidateException( "Time concluded", CandidateExceptionRedirect.EXAM_SELECT );
		}
		PaperId paperId = new PaperId( exam, paperName );
		Paper paper = paperRepository.findById( paperId ).orElse( null );
		if ( paper == null ) {
			throw new CandidateException( "Invalid paper", CandidateExceptionRedirect.EXAM_SELECT );
		}
		CandidateQuestionAnswerMapper map = candidateQuestionAnswerMapperRepository.findById( new CandidateQuestionAnswerMapperId( questionNumber, candidate, paper ) )
		.orElseThrow( () -> new CandidateException( "Invalid question requested", CandidateExceptionRedirect.ERROR ) );
		return new ExamCandidateDTO( candidate, map );
	}

	@Override
	public Candidate submitExam( ExamCandidateDTO candidateDTO ) throws CandidateException {
		if ( candidateDTO == null ) {
			throw new CandidateException( "Invalid candidate", CandidateExceptionRedirect.EXAM_SELECT );
		}
		Exam exam = examRepository.findById( candidateDTO.getExamId() )
		.orElseThrow( () -> new CandidateException( "Invalid exam selected", CandidateExceptionRedirect.EXAM_SELECT ) );
		if ( exam.getState() != ExamState.Ongoing ) {
			throw new CandidateException( "Exam is not ongoing", CandidateExceptionRedirect.EXAM_SELECT );
		}
		Candidate candidate = candidateRepository.findById( candidateDTO.getCandidateId( exam ) )
		.orElseThrow( () -> new CandidateException( "Invalid candidate", CandidateExceptionRedirect.EXAM_SELECT ) );
		if ( !candidate.getSubmitted() ) {
			candidate.setSubmitted( true );
			Candidate.score( candidate );
			return candidateRepository.save( candidate );
		} else {
			if ( candidate.getScore() < 0 ) {
				Candidate.score( candidate );
				candidateRepository.save( candidate );
			}
			throw new CandidateException( "Candidate has already submitted!", CandidateExceptionRedirect.SUBMITTED );
		}
	}

	private void updateQuestion( ExamCandidateDTO candidateDTO, Candidate candidate, Exam exam ) {
		if ( candidateDTO.getTimeUsed() != null && candidateDTO.getTimeUsed() > candidate.getTimeUsed() ) {
			candidate.setTimeUsed( candidateDTO.getTimeUsed() );
			candidate.setHasLoggedIn( true );
			if ( candidate.getTimeUsed() >= exam.getDuration() * 60 ) {
				candidate.setSubmitted( true );
			}
			candidateRepository.save( candidate );
			Paper paper = paperRepository.findById( new PaperId( exam, candidateDTO.getCurrentPaperName() ) ).orElse( null );
			if ( paper != null ) {
				CandidateQuestionAnswerMapper answeredMap = candidateQuestionAnswerMapperRepository.findById( new CandidateQuestionAnswerMapperId( candidateDTO.getCurrentPaperQuestionNumber(), candidate, paper ) ).orElse( null );
				if ( answeredMap != null ) {
					if ( answeredMap.getAnswerIndex() == null && candidateDTO.getCurrentAnswerIndex() != null ) {
						candidate.incrementAttempted();
						candidateRepository.save( candidate );
					}
					answeredMap.setAnswerIndex( candidateDTO.getCurrentAnswerIndex() );
					candidateQuestionAnswerMapperRepository.save( answeredMap );
				}
			}
		}
	}

	private ExamCandidateDTO getQuestion( Candidate candidate, Exam exam, String paperName, Integer questionNumber) throws CandidateException {
		if ( candidate.getTimeUsed() >= exam.getDuration() * 60 ) {
			candidate.setSubmitted( true );
			if ( candidate.getScore() < 0 ) {
				Candidate.score( candidate );
			}
			candidateRepository.save( candidate );
			throw new CandidateException( "Time concluded", CandidateExceptionRedirect.SUBMITTED );
		}
		PaperId paperId = new PaperId( exam, paperName );
		Paper paper = paperRepository.findById( paperId ).orElse( null );
		if ( paper == null ) {
			// Paper doesn't exist
			throw new CandidateException( "Invalid paper", CandidateExceptionRedirect.EXAM_SELECT );
		}
		if ( candidate.getSubmitted() && candidate.getScore() < 0 ) {
			Candidate.score( candidate );
			candidateRepository.save( candidate );
		}
		CandidateQuestionAnswerMapper map = candidateQuestionAnswerMapperRepository.findById( new CandidateQuestionAnswerMapperId( questionNumber, candidate, paper ) )
		.orElseThrow( () -> new CandidateException( "Invalid question", CandidateExceptionRedirect.ERROR ) );
		return new ExamCandidateDTO( candidate, map );
	}

	private void appendQuestions( Candidate candidate, Paper paper ) {
		Random rand = new Random( candidate.getCandidateId().hashCode() + paper.getPaperId().hashCode() );
		Collections.shuffle( paper.getQuestions(), rand );
		if ( candidate.getAnswerMapper() == null ) {
			candidate.setAnswerMapper( new ArrayList<>() );
		} else {
			CandidateQuestionAnswerMapperId mapperId = new CandidateQuestionAnswerMapperId( 1, candidate, paper );
			for ( CandidateQuestionAnswerMapper mapper : candidate.getAnswerMapper() ) {
				if ( mapper.getCandidateQuestionAnswerId().equals( mapperId ) ) {
					return;
				}
			}
		}
		for ( int i = 0; i < paper.getQuestionsPerCandidate(); i++ ) {
			candidate.getAnswerMapper().add( new CandidateQuestionAnswerMapper(  i + 1, candidate, paper.getQuestions().get( i ) ) );
		}
	}
}
