package com.ace.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ace.app.dto.LoginExamCandidateDTO;
import com.ace.app.entity.Candidate;
import com.ace.app.entity.Exam;
import com.ace.app.entity.Paper;
import com.ace.app.model.CandidateId;
import com.ace.app.model.ExamState;
import com.ace.app.model.PaperId;
import com.ace.app.repository.CandidateRepository;
import com.ace.app.repository.ExamRepository;
import com.ace.app.repository.PaperRepository;
import com.ace.app.service.CandidateService;

/**
 * @author Ogboru Jude
 * @version 25-June-2024
 */
@Service
public class CandidateServiceImpl implements CandidateService {
	
	@Autowired
	private ExamRepository examRepository;

	@Autowired
	private CandidateRepository candidateRepository;

	@Autowired
	private PaperRepository paperRepository;

	public LoginExamCandidateDTO login( LoginExamCandidateDTO candidateDTO ) {
		if ( candidateDTO.getExamId() == null ) {
			return null;
		}
		Exam exam = examRepository.findById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null || exam.getState() != ExamState.Ongoing ) {
			// The exam trying to be written isn't available to be written
			return null;
		}
		Candidate candidate = candidateRepository.findById( new CandidateId( exam, candidateDTO.getField1(), candidateDTO.getField2() == null ? "" : candidateDTO.getField2() ) )
			.orElse( new Candidate( candidateDTO, exam ) );
		if ( candidate.getPapers() == null || candidate.getPapers().size() != exam.getPapersPerCandidate() ) {
			List<Paper> papers = new ArrayList<>();
			candidateDTO.getPaperNames().forEach( name -> {
				Paper paper = paperRepository.findById( new PaperId( exam, name ) ).orElse( null );
				if ( paper != null ) {
					papers.add( paper );
				}
			} );
			if ( papers.size() == exam.getPapersPerCandidate() ) {
				if ( candidate.getPapers() == null ) {
					candidate.setPapers( new ArrayList<>() );
				}
				candidate.getPapers().addAll( papers );
				candidate.setHasLoggedIn( true );
			}
		} else if ( candidate.getPapers() != null && candidate.getPapers().size() == exam.getPapersPerCandidate() ) {
			candidate.setHasLoggedIn( true );
			return new LoginExamCandidateDTO( candidateRepository.save( candidate ) );
		}
		if ( exam.getRegistrationLocked() ) {
			for ( Candidate _candidate : exam.getCandidates() ) {
				if ( candidate.getCandidateId().equals( _candidate.getCandidateId() ) && !_candidate.getHasLoggedIn() ) {
					return new LoginExamCandidateDTO( candidateRepository.save( candidate ) );
				} else if ( candidate.getCandidateId().equals( _candidate.getCandidateId() ) && _candidate.getHasLoggedIn() ) {
					// The candidate has already logged in previously
					return null;
				}
			}
			// The candidate's credientials are not registered
			return null;
		}
		for ( Candidate _candidate : exam.getCandidates() ) {
			if ( candidate.getCandidateId().equals( _candidate.getCandidateId() ) && _candidate.getHasLoggedIn() ) {
				// The candidate has already logged in previously
				return null;
			}
		}
		return new LoginExamCandidateDTO( candidateRepository.save( candidate ) );
	}
}
