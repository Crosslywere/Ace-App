package com.ace.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ace.app.dto.LoginExamCandidateDTO;
import com.ace.app.entity.Exam;
import com.ace.app.model.ExamState;
import com.ace.app.service.CandidateService;
import com.ace.app.service.ExamService;

/**
 * @author Ogboru Jude
 * @version 23-June-2024
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
		model.addAttribute( "candidate", new LoginExamCandidateDTO() );
		model.addAttribute( "exams", examService.getExamsByState( ExamState.Ongoing ) );
		return "candidate/exam-select";
	}

	@PostMapping( "/exam/login" )
	public String examLogin( LoginExamCandidateDTO candidateDTO, Model model ) {
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

	@PostMapping( "/exam" )
	public String exam( LoginExamCandidateDTO candidateDTO, Model model, RedirectAttributes redirect ) {
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null || exam.getState() != ExamState.Ongoing ) {
			return "redirect:/exam/select";
		}
		candidateDTO = candidateService.login( candidateDTO );
		if ( candidateDTO == null ) {
			return "redirect:/exam/select";
		}
		if ( candidateDTO.getPaperNames().size() != exam.getPapersPerCandidate() ) {
			model.addAttribute( "exam", exam );
			model.addAttribute( "candidate", candidateDTO );
			return "candidate/exam-papers";
		}
		return "candidate/pre-exam";
	}
}
