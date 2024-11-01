package com.ace.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ace.app.entity.Candidate;
import com.ace.app.entity.Exam;
import com.ace.app.repository.CandidateRepository;
import com.ace.app.repository.ExamRepository;
import com.ace.app.service.SearchService;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private ExamRepository examRepository;

	@Autowired
	private CandidateRepository candidateRepository;

	@Override
	public List<Exam> findExamsByTitle( String title ) {
		return examRepository.findByTitleContainingIgnoreCase( title );
	}

	@Override
	public List<Candidate> findCandidatesByString( Long examId, String str ) {
		return candidateRepository.searchByExamIdAndField1( examId, str );
	}

	public Candidate findCandidate( Long examId, String field1, String field2 ) {
		return candidateRepository.findById( examRepository.findById( examId ).orElse( null ), field1, field2 ).orElse( null );
	}
}
