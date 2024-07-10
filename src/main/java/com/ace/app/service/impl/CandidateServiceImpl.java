package com.ace.app.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ace.app.dto.RegisterCandidateDTO;
import com.ace.app.entity.Candidate;
import com.ace.app.entity.Exam;
import com.ace.app.entity.Paper;
import com.ace.app.model.CandidateException;
import com.ace.app.model.CandidateExceptionType;
import com.ace.app.model.CandidateId;
import com.ace.app.model.ExamState;
import com.ace.app.model.PaperId;
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

	@Override
	public Candidate putCandidate( RegisterCandidateDTO candidateDTO ) throws CandidateException {
		if ( candidateDTO == null || candidateDTO.getExamId() == null ) {
			throw new CandidateException( "Invalid exam selected", CandidateExceptionType.INVALID_EXAM, null );
		}
		Exam exam = examRepository.findById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null || exam.getState() != ExamState.Ongoing ) {
			throw new CandidateException( "Invalid exam selected (may not be ongoing)", CandidateExceptionType.INVALID_EXAM, null );
		}
		Candidate tempCandidate = new Candidate( candidateDTO, exam );
		if ( exam.getRegistrationLocked() ) {
			Candidate candidate = candidateRepository.findById( tempCandidate.getCandidateId() ).orElse( null );
			if ( candidate != null ) {
				return candidate;
			} else {
				throw new CandidateException( "Invalid Credentials", CandidateExceptionType.INVALID_CREDENTIALS, candidateDTO );
			}
		} else {
			Candidate candidate = candidateRepository.findById( tempCandidate.getCandidateId() ).orElse( null );
			if ( candidate != null ) {
				return candidate;
			} else {
				return candidateRepository.save( tempCandidate );
			}
		}
	}

	@Override
	public Candidate loginCandidate( RegisterCandidateDTO candidateDTO ) throws CandidateException {
		if ( candidateDTO == null || candidateDTO.getExamId() == null ) {
			throw new CandidateException( "Invalid exam selected", CandidateExceptionType.INVALID_EXAM, null );
		}
		Exam exam = examRepository.findById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null || exam.getState() != ExamState.Ongoing ) {
			throw new CandidateException( "Invalid exam selected", CandidateExceptionType.INVALID_EXAM, null );
		}
		Candidate candidate = candidateRepository.findById( new Candidate( candidateDTO, exam ).getCandidateId() ).orElse( null );
		if ( candidate == null ) {
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
				throw new CandidateException( "Invalid number of papers selected", CandidateExceptionType.INVALID_CANDIDATE, candidateDTO );
			} else {
				for ( Paper paper : exam.getPapers() ) {
					if ( paper.getManditory() ) {
						if ( !candidate.getPapers().contains( paper ) ) {
							throw new CandidateException( "Please select manditory paper(" + paper.getName() + ")", CandidateExceptionType.INVALID_PAPER_COUNT, candidateDTO );
						}
					}
				}
				candidate.setHasLoggedIn( true );
				return candidateRepository.save( candidate );
			}
		} else {
			for ( Paper paper : exam.getPapers() ) {
				if ( paper.getManditory() ) {
					if ( !candidate.getPapers().contains( paper ) ) {
						throw new CandidateException( "Please select manditory paper(" + paper.getName() + ")", CandidateExceptionType.INVALID_PAPER_COUNT, candidateDTO );
					}
				}
			}
			candidate.setHasLoggedIn( true );
			return candidateRepository.save( candidate );
		}
	}
}
