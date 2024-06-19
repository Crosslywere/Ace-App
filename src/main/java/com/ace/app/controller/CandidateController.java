package com.ace.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ace.app.dto.LoginExamCandidateDTO;
import com.ace.app.model.ExamState;
import com.ace.app.service.ExamService;

/**
 * @author Ogboru Jude
 * @version 19-June-2024
 */
@Controller
public class CandidateController {

	@Autowired
	private ExamService examService;
	
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
}
