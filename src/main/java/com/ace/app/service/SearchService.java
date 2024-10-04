package com.ace.app.service;

import java.util.List;

import com.ace.app.entity.Candidate;
import com.ace.app.entity.Exam;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
public interface SearchService {

	List<Exam> findExamsByTitle( String title );

	List<Candidate> findCandidatesByString( Long examId, String str );

	Candidate findCandidate( Long examId, String field1, String field2 );
	
}
