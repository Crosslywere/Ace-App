package com.ace.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ace.app.dto.BaseCandidateDTO;
import com.ace.app.dto.ExamCandidateDTO;
import com.ace.app.dto.ExamCandidateRateDTO;
import com.ace.app.dto.RegisterCandidateDTO;
import com.ace.app.entity.Candidate;
import com.ace.app.entity.Exam;
import com.ace.app.model.CandidateException;
import com.ace.app.model.ExamState;
import com.ace.app.service.CandidateService;
import com.ace.app.service.ExamService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

	private static final String[] COOKIE_NAMES = { "examId", "field1", "field2" };
	
	/**
	 * @return A redirect to the start of the exam login sequence ie the {@code /exam/select}
	 */
	@GetMapping( { "/exam", "/exam/" } )
	public String examIndex() {
		return "redirect:/exam/select";
	}

	/**
	 * The template for selecting the exam to be taken
	 * @param model Provided by Springboot to pass arguments to the template
	 * @return The template for selecting the exam to be taken
	 */
	@GetMapping( "/exam/select" )
	public String examSelect( HttpServletResponse response, Model model ) {
		// Resetting all cookies
		resetCookies( response );
		model.addAttribute( "candidate", new RegisterCandidateDTO() );
		model.addAttribute( "exams", examService.getExamsByState( ExamState.Ongoing ) );
		return "candidate/exam-select";
	}

	/**
	 * The template for entering the candidate's credentials for their selected exam
	 * @param candidateDTO A structure that contains all the necessary candidate data
	 * @param model Used to pass arguments to the template
	 * @return The template for entering the candidate's login details
	 */
	@PostMapping( "/exam/login" )
	public String examLogin( RegisterCandidateDTO candidateDTO, HttpServletResponse response, Model model ) {
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam != null && exam.getState() == ExamState.Ongoing ) {
			// Adding examId as cookie
			var examIdCookie = new Cookie( COOKIE_NAMES[0], Long.toString( exam.getExamId() ) );
			examIdCookie.setPath( "/" );
			examIdCookie.setMaxAge( Math.max( exam.getDuration(), 10 ) * 60 );
			examIdCookie.setHttpOnly( true );
			response.addCookie( examIdCookie );

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
	 * Submits the current question answered in the candidateDTO object then gets the
	 * question requested
	 * @param candidateDTO A structure that contains all the necessary candidate data
	 * @param model Used to pass arguments to the template
	 * @return The template for selecting the candidate's papers
	 */
	@PostMapping( "/exam/papers" )
	public String examPapers( RegisterCandidateDTO candidateDTO, HttpServletRequest request, HttpServletResponse response, Model model ) {
		candidateDTO.setExamId( retriveCookiesIntoCandidate( request, null ) );
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null || exam.getState() != ExamState.Ongoing ) {
			return "redirect:/exam/select";
		}
		Candidate candidate = null;
		try {
			candidate = candidateService.putCandidate( candidateDTO );
		} catch ( CandidateException e ) {
			return handleCandidateException( e, candidateDTO, exam, model, response );
		}
		candidateDTO = new RegisterCandidateDTO( candidate );
		if ( candidateDTO.getPaperNames().size() == exam.getPapersPerCandidate() ) {
			return candidateValidate( candidateDTO, request, response, model );
		}
		insertCookies( response, candidateDTO );
		model.addAttribute( "candidate", candidateDTO );
		model.addAttribute( "exam", exam );
		return "candidate/exam-papers";
	}

	/**
	 * The template that explains the rules of the exam before the candidate starts
	 * @param candidateDTO A structure that contains all the necessary candidate data
	 * @param model Used to pass arguments to the template
	 * @return The preamble template
	 */
	@PostMapping( "/exam/validate" )
	public String candidateValidate( RegisterCandidateDTO candidateDTO, HttpServletRequest request, HttpServletResponse response, Model model ) {
		candidateDTO.setExamId( retriveCookiesIntoCandidate( request, candidateDTO ) );
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null || exam.getState() != ExamState.Ongoing ) {
			return "redirect:/exam/select";
		}
		Candidate candidate = null;
		try {
			candidate = candidateService.loginCandidate( candidateDTO );
		} catch ( CandidateException e ) {
			return handleCandidateException( e, candidateDTO, exam, model, response );
		}
		if ( candidate == null ) {
			return "redirect:/exam/select";
		}
		insertCookies( response, candidateDTO );
		model.addAttribute( "exam", exam );
		model.addAttribute( "candidate", new ExamCandidateDTO( candidate ) );
		return "candidate/exam-preamble";
	}

	/**
	 * The template that allows a candidate answer questions and returns the template that has the requested question
	 * @param paperName The name of the paper to be served
	 * @param number The question number of the question to be served
	 * @param candidateDTO A structure that contains all the necessary candidate data
	 * @param model Used to pass arguments to the template
	 * @return The exam question template for the selected exam
	 */
	// todo retrieve reset
	@PostMapping( "/exam/{paper}/{number}" )
	public String getQuestion( @PathVariable( "paper" ) String paperName, @PathVariable( "number" ) Integer number, ExamCandidateDTO candidateDTO, HttpServletRequest request, HttpServletResponse response, Model model ) {
		candidateDTO.setExamId( retriveCookiesIntoCandidate( request, candidateDTO ) );
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null ) {
			return "redirect:/exam/select";
		}
		try {
			candidateDTO = candidateService.submitQuestion( candidateDTO, paperName, number );
		} catch ( CandidateException e ) {
			return handleCandidateException( e, candidateDTO, exam, model, response );
		}
		// resetCookies( response );
		insertCookies( response, candidateDTO, exam );
		model.addAttribute( "exam", exam );
		model.addAttribute( "candidate", candidateDTO );
		return "candidate/exam-question";
	}

	/**
	 * A confirmation template for submitting the exam
	 * @param candidateDTO A structure that contains all the necessary candidate data
	 * @param model Used to pass arguments to the template
	 * @return A template that allows the candidate to submit or review their previous answers
	 */
	@PostMapping( "/exam/submit" )
	public String submitConfirm( ExamCandidateDTO candidateDTO, HttpServletRequest request, HttpServletResponse response, Model model ) {
		candidateDTO.setExamId( retriveCookiesIntoCandidate( request, candidateDTO ) );
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null ) {
			return "redirect:/exam/select";
		}
		try {
			candidateService.answerQuestion( candidateDTO );
		} catch ( CandidateException e ) {
			return handleCandidateException( e, candidateDTO, exam, model, response );
		}
		insertCookies( response, candidateDTO );
		candidateDTO.setCurrentAsPrevQuestionPath();
		model.addAttribute( "candidate", candidateDTO );
		return "candidate/exam-submit";
	}

	/**
	 * Sets the candidate as submitted and returns the template to affirm this
	 * @param candidateDTO A structure that contains all the necessary candidate data
	 * @param model Used to pass arguments to the template
	 * @return The template for a submitted candidate
	 */
	// todo retreive reset
	@PostMapping( "/exam/submitted" )
	public String submit( ExamCandidateDTO candidateDTO, HttpServletRequest request, HttpServletResponse response, Model model ) {
		candidateDTO.setExamId( retriveCookiesIntoCandidate( request, candidateDTO ) );
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null ) {
			return "redirect:/exam/select";
		}
		Candidate candidate = null;
		try {
			candidate = candidateService.submitExam( candidateDTO );
		} catch ( CandidateException e ) {
			return handleCandidateException( e, candidateDTO, exam, model, response );
		}
		insertCookies( response, candidateDTO );
		model.addAttribute( "exam", exam );
		model.addAttribute( "candidate", candidate );
		model.addAttribute( "rateCandidate", new ExamCandidateRateDTO( candidate ) );
		return "candidate/exam-submitted";
	}

	// TODO implement
	@PostMapping( "/exam/rate" )
	public String rate( ExamCandidateRateDTO candidateDTO, Model model ) {
		Exam exam = examService.getExamById( candidateDTO.getExamId() ).orElse( null );
		if ( exam == null ) {

		}
		return "";
	}

	/**
	 * Processes the exception and returns the appropriate template
	 * <p>ie. A graceful failure of the program on the candidate's side
	 * @param <DTO> A class that extends the {@code BaseCandidateDTO} class
	 * @param e The exception to be handled
	 * @param candidateDTO A structure that contains all the necessary candidate data
	 * @param exam The targeted exam
	 * @param model Used to pass arguments to the template
	 * @return The redirect path specified by the exception
	 */
	private <DTO extends BaseCandidateDTO> String handleCandidateException( CandidateException e, DTO candidateDTO, Exam exam, Model model, HttpServletResponse response ) {
		model.addAttribute( "errorMessage", e.getMessage() );
			switch ( e.getRedirect() ) {
				case EXAM_SELECT -> {
					resetCookies( response );
					model.addAttribute( "candidate", candidateDTO );
					model.addAttribute( "exams", examService.getExamsByState( ExamState.Ongoing ) );
				}
				case PAPER_SELECT -> {
					insertCookies( response, candidateDTO );
					// TODO add paper select cookies
					model.addAttribute( "candidate", candidateDTO );
					model.addAttribute( "exam", exam );
				}
				case LOGIN_PAGE -> {
					if ( exam == null ) {
						return "redirect:/exam";
					}
					resetCookies( response );

					model.addAttribute( "field1", exam.getLoginField1() );
					model.addAttribute( "field1Desc", exam.getLoginField1Desc() );
					model.addAttribute( "field2", exam.getLoginField2() );
					model.addAttribute( "field2Desc", exam.getLoginField2Desc() );
					model.addAttribute( "candidate", candidateDTO );
				}
				case SUBMITTED -> {
					resetCookies( response );
					model.addAttribute( "candidate", candidateDTO );
				}
				case ERROR -> {
				}
			}
		return e.getRedirect().getPath();
	}

	/**
	 * Resets all the cookies in the browser session.
	 * @param response The response to use to clear the browser session.
	 */
	private void resetCookies( HttpServletResponse response ) {
		for ( var name : COOKIE_NAMES ) {
			var cookie = new Cookie( name, null );
			cookie.setPath( "/" );
			cookie.setHttpOnly( true );
			cookie.setMaxAge( 0 );
			response.addCookie( cookie );
		}
	}

	/**
	 * Inserts the candidate's details into the browsers session.
	 * @param <DTO> A class that extends the {@code BaseCandidateDTO} class
	 * @param response The response to add the cookies to
	 * @param candidateDTO The candidate with the details to be used
	 */
	private <DTO extends BaseCandidateDTO> void insertCookies( HttpServletResponse response, DTO candidateDTO ) {
		String field1 = candidateDTO.getField1().replaceAll( " ", "[32]" );
		var field1Cookie = new Cookie( COOKIE_NAMES[1], field1 );
		field1Cookie.setMaxAge( 10 * 60 );
		field1Cookie.setPath( "/" );
		field1Cookie.setHttpOnly( true );
		response.addCookie( field1Cookie );
		String field2 = candidateDTO.getField2().replaceAll( " ", "\\[[3][2]\\]" );
		var field2Cookie = new Cookie( COOKIE_NAMES[2], field2 );
		field2Cookie.setMaxAge( 10 * 60 );
		field2Cookie.setPath( "/" );
		field2Cookie.setHttpOnly( true );
		response.addCookie( field2Cookie );
	}

	/**
	 * Inserts the candidate's details into the browsers session.
	 * @param response The response to add the cookies to
	 * @param candidateDTO The candidate with the details to be used
	 */
	private void insertCookies( HttpServletResponse response, ExamCandidateDTO candidateDTO, Exam exam ) {
		String field1 = candidateDTO.getField1().replaceAll( " ", "[32]" );
		String field2 = candidateDTO.getField2().replaceAll( " ", "[32]" );
		int maxAge = ( ( exam.getDuration() + 10 ) * 60 ) - candidateDTO.getTimeUsed().intValue();

		var examIdCookie = new Cookie( COOKIE_NAMES[1], exam.getExamId().toString() );
		examIdCookie.setMaxAge( maxAge );
		examIdCookie.setPath( "/" );
		examIdCookie.setHttpOnly( true );
		response.addCookie( examIdCookie );

		var field1Cookie = new Cookie( COOKIE_NAMES[1], field1 );
		field1Cookie.setMaxAge( maxAge );
		field1Cookie.setPath( "/" );
		field1Cookie.setHttpOnly( true );
		response.addCookie( field1Cookie );

		var field2Cookie = new Cookie( COOKIE_NAMES[2], field2 );
		field2Cookie.setMaxAge( maxAge );
		field2Cookie.setPath( "/" );
		field2Cookie.setHttpOnly( true );
		response.addCookie( field2Cookie );
	}

	/**
	 * 
	 * @param <DTO> A class that extends the {@code BaseCandidateDTO} class
	 * @param request The request to extract the cookies from
	 * @param candidateDTO The candidate to place the details into
	 * @return The {@code examId} in the browser's session
	 */
	private <DTO extends BaseCandidateDTO> Long retriveCookiesIntoCandidate( HttpServletRequest request, DTO candidateDTO ) {
		Long examId = 0L;
		for ( var cookie : request.getCookies() ) {
			if ( cookie.getName().equals( COOKIE_NAMES[0] ) ) {
				examId = Long.valueOf( cookie.getValue() );
			}
			if ( candidateDTO != null ) {
				if ( cookie.getName().equals( COOKIE_NAMES[1] ) ) {
					candidateDTO.setField1( cookie.getValue().replaceAll( "\\[[3][2]\\]", " " ) );
				}
				if ( cookie.getName().equals( COOKIE_NAMES[2] ) ) {
					candidateDTO.setField2( cookie.getValue().replaceAll( "\\[[3][2]\\]", " " ) );
				}
			}
		}
		return examId;
	}
}
