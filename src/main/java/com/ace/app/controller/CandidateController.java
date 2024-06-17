package com.ace.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Ogboru Jude
 * @version 21-May-2024
 */
@Controller
public class CandidateController {
	
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
		return "candidate/exam-select";
	}
}
