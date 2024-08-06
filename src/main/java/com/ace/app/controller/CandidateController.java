package com.ace.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ace.app.dto.BaseCandidateDTO;
import com.ace.app.dto.ExamCandidateDTO;
import com.ace.app.dto.RegisterCandidateDTO;
import com.ace.app.entity.Candidate;
import com.ace.app.entity.Exam;
import com.ace.app.model.CandidateException;
import com.ace.app.model.ExamState;
import com.ace.app.service.CandidateService;
import com.ace.app.service.ExamService;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Controller
public class CandidateController {

	@Autowired
	private ExamService examService;

	@Autowired
	private CandidateService candidateService;
	
	/**
	 * @return A redirect to the start of the exam login sequence ie the {@code /exam/select}
	 */
	@GetMapping( { "/exam", "/exam/" } )
	public String examIndex() {
		return "redirect:/exam/select";
	}

	/**
	 * @param model Provided by Springboot to pass arguments to the template
	 * @return The template for selecting the exam to be taken
	 */
	@GetMapping( "/exam/select" )
	public String examSelect( Model model ) {
		model.addAttribute( "candidate", new RegisterCandidateDTO() );
		model.addAttribute( "exams", examService.getExamsByState( ExamState.Ongoing ) );
		return "candidate/exam-select";
	}

	/**
	 * @param candidateDTO
	 * @param model
	 * @return The template for entering the candidate's login details
	 */
	@PostMapping( "/exam/login" )
	public String examLogin( RegisterCandidateDTO candidateDTO, Model model ) {
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam != null && exam.getState() == ExamState.Ongoing ) {
			model.addAttribute( "field1", exam.getLoginField1() );
			model.addAttribute( "field1Desc", exam.getLoginField1Desc() );
			model.addAttribute( "field2", exam.getLoginField2() );
			model.addAttribute( "field2Desc", exam.getLoginField2Desc() );
			model.addAttribute( "candidate", candidateDTO );
			return "candidate/exam-login";
		}
		return "redirect:/exam/select";
	}

	/**
	 * In the case that the candidate has not selected the appropriate
	 * number of questions the candidate returns the template else redirects
	 * to the preamble page.
	 * @param candidateDTO
	 * @param model
	 * @param redirect
	 * @return The template for selecting the candidate's papers
	 */
	@PostMapping( "/exam/papers" )
	public String examPapers( RegisterCandidateDTO candidateDTO, Model model ) {
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null || exam.getState() != ExamState.Ongoing ) {
			return "redirect:/exam/select";
		}
		Candidate candidate = null;
		try {
			candidate = candidateService.putCandidate( candidateDTO );
		} catch ( CandidateException e ) {
			return handleCandidateException( e, candidateDTO, exam, model );
		}
		if ( candidate == null ) {
			return "redirect:/exam/select";
		} else {
			candidateDTO = new RegisterCandidateDTO( candidate );
			if ( candidateDTO.getPaperNames().size() == exam.getPapersPerCandidate() ) {
				return candidateValidate( candidateDTO, model );
			}
			model.addAttribute( "candidate", candidateDTO );
			model.addAttribute( "exam", exam );
			return "candidate/exam-papers";
		}
	}

	/**
	 * 
	 * @param candidateDTO
	 * @param model
	 * @return The preamble template
	 */
	@PostMapping( "/exam/validate" )
	public String candidateValidate( RegisterCandidateDTO candidateDTO, Model model ) {
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null || exam.getState() != ExamState.Ongoing ) {
			return "redirect:/exam/select";
		}
		Candidate candidate = null;
		try {
			candidate = candidateService.loginCandidate( candidateDTO );
		} catch ( CandidateException e ) {
			return handleCandidateException( e, candidateDTO, exam, model );
		}
		if ( candidate == null ) {
			return "redirect:/exam/select";
		}
		model.addAttribute( "exam", exam );
		model.addAttribute( "candidate", new ExamCandidateDTO( candidate ) );
		return "candidate/exam-preamble";
	}

	/**
	 * 
	 * @param candidateDTO
	 * @param model
	 * @return The exam question template for the selected exam
	 */
	@PostMapping( "/exam/{paper}/{number}" )
	public String getQuestion( @PathVariable( "paper" ) String paperName, @PathVariable( "number" ) Integer number, ExamCandidateDTO candidateDTO, Model model ) {
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null ) {
			return "redirect:/exam/select";
		}
		if ( candidateDTO.getCurrentAnswerIndex() == null ) {
			try {
				candidateDTO = candidateService.getExamCandidateQuestion( candidateDTO, paperName, number );
			} catch ( CandidateException e ) {
				return handleCandidateException( e, candidateDTO, exam, model );
			}
			model.addAttribute( "exam", exam );
			model.addAttribute( "candidate", candidateDTO );
			return "candidate/exam-question";
		}
		try {
			candidateDTO = candidateService.submitQuestion( candidateDTO, paperName, number );
		} catch ( CandidateException e ) {
			return handleCandidateException( e, candidateDTO, exam, model );
		}
		model.addAttribute( "exam", exam );
		model.addAttribute( "candidate", candidateDTO );
		return "candidate/exam-question";
	}


	@PostMapping( "/exam/submit" )
	public String submitConfirm( ExamCandidateDTO candidateDTO, Model model ) {
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null ) {
			return "redirect:/exam/select";
		}
		try {
			candidateService.answerQuestion( candidateDTO );
		} catch ( CandidateException e ) {
			return handleCandidateException( e, candidateDTO, exam, model );
		}
		model.addAttribute( "candidate", candidateDTO );
		return "candidate/exam-submit";
	}

	@PostMapping( "exam/submitted" )
	public String submit( ExamCandidateDTO candidateDTO, Model model ) {
		model.addAttribute( "candidate", candidateDTO );
		return "candidate/exam-submitted";
	}

	private <T extends BaseCandidateDTO> String handleCandidateException( CandidateException e, T candidateDTO, Exam exam, Model model ) {
		model.addAttribute( "errorMessage", e.getMessage() );
			switch ( e.getRedirect() ) {
				case EXAM_SELECT -> {
					model.addAttribute( "candidate", candidateDTO );
					model.addAttribute( "exams", examService.getExamsByState( ExamState.Ongoing ) );
				}
				case PAPER_SELECT -> {
					model.addAttribute( "candidate", candidateDTO );
					model.addAttribute( "exam", exam );
				}
				case LOGIN_PAGE -> {
					model.addAttribute( "field1", exam.getLoginField1() );
					model.addAttribute( "field1Desc", exam.getLoginField1Desc() );
					model.addAttribute( "field2", exam.getLoginField2() );
					model.addAttribute( "field2Desc", exam.getLoginField2Desc() );
					model.addAttribute( "candidate", candidateDTO );
				}
				case SUBMITTED -> {
					model.addAttribute( "candidate", candidateDTO );
				}
				case ERROR -> {
				}
			}
		return e.getRedirect().getPath();
	}
}
