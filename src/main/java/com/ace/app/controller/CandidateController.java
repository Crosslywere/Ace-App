package com.ace.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	@GetMapping( "/exam" )
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
	public String examPapers( RegisterCandidateDTO candidateDTO, Model model, RedirectAttributes redirect ) {
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null || exam.getState() != ExamState.Ongoing ) {
			return "redirect:/exam/select";
		}
		Candidate candidate = null;
		try {
			candidate = candidateService.putCandidate( candidateDTO );
		} catch ( CandidateException e ) {
			switch ( e.getType() ) {
				case INVALID_CREDENTIALS -> {
					if ( e.getObject() != null && e.getObject() instanceof RegisterCandidateDTO ) {
						candidateDTO = ( RegisterCandidateDTO )e.getObject();
						model.addAttribute( "errorMessage", e.getMessage() );
						return examLogin( candidateDTO, model );
					}
				}
				default -> {
					return "redirect:/exam/select";
				}
			}
		}
		if ( candidate == null ) {
			return "redirect:/exam/select";
		}
		if ( candidate.getPapers().size() == exam.getPapersPerCandidate() ) {
			// TODO Create CandidateDTO for the purpose of preamble
			model.addAttribute( "candidate", null );
			return "candidate/exam-preamble";
		} else {
			candidateDTO = new RegisterCandidateDTO( candidate );
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
			switch ( e.getType() ) {
				// TODO Handle errors
				case INVALID_CANDIDATE -> {
					return "redirect:/exam/select";
				}
				case INVALID_CREDENTIALS -> {
					return "redirect:/exam/select";
				}
				default -> {
					System.out.println( e.getMessage() );
					return "redirect:/exam/select";
				}
			}
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
	public String getQuestion( ExamCandidateDTO candidateDTO, Model model ) {
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null ) {
			return "redirect:/exam/select";
		}
		model.addAttribute( "exam", exam );
		model.addAttribute( "candidate", candidateDTO );
		return "candidate/exam-question";
	}
}
