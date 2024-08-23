package com.ace.app.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ace.app.dto.CreateExamDTO;
import com.ace.app.dto.ModifyOExamDTO;
import com.ace.app.dto.ModifySExamDTO;
import com.ace.app.dto.QueryDTO;
import com.ace.app.entity.Exam;
import com.ace.app.entity.ExportConfig;
import com.ace.app.model.ExamState;
import com.ace.app.repository.ExamConfigRepository;
import com.ace.app.service.ExamService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller for the dashboard on the server machine
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Controller
public class DashboardController {

	@Autowired
	private ExamService examService;

	@Autowired
	private ExamConfigRepository configRepo;

	/**
	 * Checks if the request is from either {@code localhost} or {@code 127.0.0.1}
	 * @param request The request to check the url
	 * @return {@code true} if the request contains {@code localhost} or {@code 128.0.0.1}
	 */
	public static boolean isLocalhost( HttpServletRequest request ) {
		String url = request.getRequestURL().toString();
		return url.contains( "localhost" ) || url.contains( "127.0.0.1" );
	}

	/**
	 * Redirects to the proper route based on the request
	 * @param request Used to determine if request comes from the server
	 * @return Redirect to the proper route
	 */
	@GetMapping( "/" )
	public String indexRouter( HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			return "redirect:/scheduled";
		}
		return "redirect:/exam";
	}

	/**
	 * Routing for the {@code /scheduled} get mapping
	 * @param model Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if request comes from the server
	 * @return The template for the scheduled exams
	 */
	@GetMapping( "/scheduled" )
	public String scheduled( Model model, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			examService.sortExams();
			List<Exam> exams = examService.getExamsByState( ExamState.Scheduled );
			model.addAttribute( "exams", exams );
			model.addAttribute( "countOngoing", examService.countExamsByState( ExamState.Ongoing ) );
			model.addAttribute( "countScheduled", exams.size() );
			model.addAttribute( "countRecorded", examService.countExamsByState( ExamState.Recorded ) );
			return "dashboard/scheduled";
		}
		return "redirect:/exam";
	}

	/**
	 * Routing for the {@code /recorded} get mapping
	 * @param model Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if request comes from the server
	 * @return The template for the recorded exams
	 */
	@GetMapping( "/recorded" )
	public String recorded( Model model, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			List<Exam> exams = examService.getExamsByState( ExamState.Recorded );
			model.addAttribute( "exams", exams );
			model.addAttribute( "countOngoing", examService.countExamsByState( ExamState.Ongoing ) );
			model.addAttribute( "countScheduled", examService.countExamsByState( ExamState.Scheduled ) );
			model.addAttribute( "countRecorded", exams.size() );
			return "dashboard/recorded";
		}
		return "redirect:/exam";
	}

	/**
	 * Routing for the {@code /ongoing} get mapping
	 * @param model Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if request comes from the server
	 * @return The template for the ongoing exams if there are ongoing exams else redirects to the recorded exams page
	 */
	@GetMapping( "/ongoing" )
	public String ongoing( Model model, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			examService.sortExams();
			List<Exam> exams = examService.getExamsByState( ExamState.Ongoing );
			if ( exams.isEmpty() ) {
				return "redirect:/recorded";
			}
			model.addAttribute( "exams", exams );
			return "dashboard/ongoing";
		}
		return "redirect:/exam";
	}

	/**
	 * Gets a list of exams by their title similar to the search query 
	 * @param title The title to search for
	 * @param model  Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if request comes from the server
	 * @return A template containing results from the search
	 */
	@PostMapping( "/search" )
	public String search( @Param( "search" ) String search, Model model, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			if ( search == null || search.isBlank() ) {
				return "redirect:/advanced-search";
			}
			List<Exam> exams = examService.getExamsByTitleLike( search );
			model.addAttribute( "countOngoing", examService.countExamsByState( ExamState.Ongoing ) );
			model.addAttribute( "results", exams );
			return "dashboard/search";
		}
		return "redirect:/exam";
	}

	/**
	 * TODO
	 * Get mapping for the advanced search template
	 * @param model Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if request comes from the server
	 * @return The template for advanced searches
	 */
	@GetMapping( "/advanced-search" )
	public String search( Model model, HttpServletRequest request ) {
		// TODO implement
		if ( isLocalhost( request ) ) {
			model.addAttribute( "countOngoing", examService.countExamsByState( ExamState.Ongoing ) );
			model.addAttribute( "searchQuery", new QueryDTO() );
			return "dashboard/advanced-search/search";
		}
		return "redirect:/exam";
	}

	/**
	 * TODO
	 * @param searchQuery The search configuration object
	 * @param model  Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if request comes from the server
	 * @return A template containing results from the search
	 */
	@PostMapping( "/advanced-search" )
	public String search( QueryDTO query, Model model, HttpServletRequest request ) {
		// TODO implement
		if ( isLocalhost( request ) ) {
			return "dashboard/advanced-search/search-result";
		}
		return "redirect:/exam";
	}

	/**
	 * The get mapping for the {@code /create} route.
	 * Sets up the template by adding the appropriate data transfer object to
	 * the model.
	 * @param model Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if request comes from the server
	 * @return The create exam template
	 */
	@GetMapping( "/create" )
	public String create( Model model, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			if ( model.getAttribute( "exam" ) == null ) {
				model.addAttribute( "exam", new CreateExamDTO() );
			}
			model.addAttribute( "countOngoing", examService.countExamsByState( ExamState.Ongoing ) );
			return "dashboard/create";
		}
		return "redirect:/exam";
	}

	/**
	 * Extracts the exam papers from the document
	 * @param examDTO The exam to be created containing all information
	 * @param model Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if request comes from the server
	 * @param redirect Provided by Springboot to pass arguments to the redirected route
	 * @return A template containing the the exam to be created as is
	 */
	@PostMapping( "/create/review" )
	public String reviewExam( CreateExamDTO examDTO, Model model, HttpServletRequest request, RedirectAttributes redirect ) {
		if ( isLocalhost( request ) ) {
			if ( !examDTO.parsePaperDocument() ) {
				redirect.addFlashAttribute( "exam", examDTO );
				return "redirect:/create";
			}
			model.addAttribute( "countOngoing", examService.countExamsByState( ExamState.Ongoing ) );
			model.addAttribute( "exam", examDTO );
			return "dashboard/review-create";
		}
		return "redirect:/exam";
	}

	/**
	 * Takes the incoming {@code examDTO} and extracts the candidates from the {@code candidatesLoginInfoDocument} .csv file
	 * @param examDTO The exam to be created containing all information
	 * @param model Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if request comes from the server
	 * @return A template similar to {@link #reviewExam(CreateExamDTO, Model, HttpServletRequest, RedirectAttributes)}
	 * but with the candidates extracted if any
	 */
	@PostMapping( "/create/review/candidates" ) 
	public String reviewCandidate( CreateExamDTO examDTO, Model model, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			examDTO.parseCandidateLoginInfoDocument();
			model.addAttribute( "countOngoing", examService.countExamsByState( ExamState.Ongoing ) );
			model.addAttribute( "exam", examDTO );
			return "dashboard/review-create";
		}
		return "redirect:/exam";
	}

	/**
	 * Creates an exam from the {@code examDTO} if possible on the database
	 * @param examDTO Data containing the details with the exam to be created
	 * @param request Used to determine if request comes from the server
	 * @return A redirect to the scheduled page
	 */
	@PostMapping( "/create" )
	public String createExam( CreateExamDTO examDTO, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			examService.createExam( examDTO );
			return "redirect:/scheduled";
		}
		return "redirect:/exam";
	}

	/**
	 * Get mapping for the template for modifying an exam
	 * @param examId An integer that is the ID of the exam to be modified 
	 * @param model Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if request comes from the server
	 * @return The appropriate template for an exam based on the exams state
	 */
	@GetMapping( "/modify/{examId}" )
	public String modify( @PathVariable( "examId" ) Long examId, Model model, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			Exam exam = examService.getExamById( examId ).orElse( null );
			if ( exam == null ) {
				return "redirect:/scheduled";
			}
			model.addAttribute( "countOngoing", examService.countExamsByState( ExamState.Ongoing ) );
			switch ( exam.getState() ) {
				case Scheduled -> {
					ModifySExamDTO examDTO = new ModifySExamDTO( exam );
					model.addAttribute( "exam", examDTO );
					return "dashboard/modify-scheduled";
				}
				case Ongoing -> {
					ModifyOExamDTO examDTO = new ModifyOExamDTO( exam );
					model.addAttribute( "exam", examDTO );
					return "dashboard/modify-ongoing";
				}
				case Recorded -> {
					return "redirect:/export/" + examId.toString();
				}
			}
		}
		return "redirect:/exam";
	}

	/**
	 * Post mapping for confirming the changes to be made to the exam
	 * @param examDTO The exam to be modified containing all information
	 * @param model Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if request comes from the server
	 * @return The template containing the changes made to the exam as is
	 */
	@PostMapping( "/modify/scheduled/review" )
	public String modifyScheduled( ModifySExamDTO examDTO, Model model, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			Exam exam = examService.getExamById( examDTO.getExamId() ).orElse( null );
			if ( exam != null && exam.getState() == ExamState.Scheduled ) {
				model.addAttribute( "countOngoing", examService.countExamsByState( ExamState.Ongoing ) );
				model.addAttribute( "showPapers", examDTO.parsePaperDocument() );
				model.addAttribute( "showCandidates", examDTO.parseCandidateLoginInfoDocument() );
				model.addAttribute( "exam", examDTO );
				return "dashboard/review-modify-scheduled";
			}
			return "redirect:/scheduled";
		}
		return "redirect:/exam";
	}

	/**
	 * Post mapping to review changes to ongoing exams before comitting them to the database
	 * @param examDTO The exam to be modified containing all necessary information
	 * @param model Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if the request comes from the server
	 * @return The template containing the changes made to the exam for review
	 */
	@PostMapping( "/modify/ongoing/review" )
	public String modifyOngoing( ModifyOExamDTO examDTO, Model model, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			Exam exam = examService.getExamById( examDTO.getExamId() ).orElse( null );
			if ( exam != null && exam.getState() == ExamState.Ongoing ) {
				model.addAttribute( "countOngoing", examService.countExamsByState( ExamState.Ongoing ) );
				model.addAttribute( "exam", examDTO );
				return "dashboard/review-modify-ongoing";
			}
			return "redirect:/ongoing";
		}
		return "redirect:/exam";
	}

	/**
	 * Post mapping to update modified scheduled exam specified by the {@code examDTO}
	 * @param examDTO A data transfer object that contains the details for the exam to be updated
	 * @param request Used to determine if the request comes from the server
	 * @return A redirect to the scheduled template
	 */
	@PostMapping( "/modify/scheduled" )
	public String modifyScheduled( ModifySExamDTO examDTO, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			Exam exam = examService.getExamById( examDTO.getExamId() ).orElse( null );
			if ( exam != null && exam.getState() == ExamState.Scheduled ) {
				examService.update( examDTO );
			}
			return "redirect:/scheduled";
		}
		return "redirect:/exam";
	}

	/**
	 * Post mapping to update a modified ongoing exam specified by the {@code examDTO}
	 * @param examDTO A data transfer object that contains the details for the exam to be updated
	 * @param request Used to determine if the request comes from the server
	 * @return A redirect back to the ongoing exam template
	 */
	@PostMapping( "/modify/ongoing" )
	public String modifyOngoing( ModifyOExamDTO examDTO, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			Exam exam = examService.getExamById( examDTO.getExamId() ).orElse( null );
			if ( exam != null && exam.getState() == ExamState.Ongoing ) {
				examService.update( examDTO );
			}
			return "redirect:/ongoing";
		}
		return "redirect:/exam";
	}

	/**
	 * Get mapping to begin the configuration of the exam details to be exported
	 * @param examId The examId of the exam to be exported
	 * @param model Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if request comes from the server
	 * @return A template containing the parameters to be configured for the exam's export
	 */
	@GetMapping( "/export/{examId}")
	public String export( @PathVariable( "examId" ) Long examId, Model model, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			Exam exam = examService.getExamById( examId ).orElse( null );
			if ( exam == null || exam.getState() != ExamState.Recorded ) {
				return "redirect:/recorded";
			}
			ExportConfig config = configRepo.findById( ( byte )1 ).orElse( new ExportConfig( exam ) );
			config.setExamId( exam.getExamId() );
			model.addAttribute( "countOngoing", examService.countExamsByState( ExamState.Ongoing ) );
			model.addAttribute( "config", config );
			model.addAttribute( "exam", exam );
			return "dashboard/export";
		}
		return "redirect:/exam";
	}

	/**
	 * Post mapping to get the link for downloading the exams details as a .csv file using the configured template
	 * @param config  The configuration to be used for the export.
	 * @param model Provided by Springboot to pass arguments to the template
	 * @param request Used to determine if the request comes from the server
	 * @return A template containing the link to download the exported exam
	 */
	@PostMapping( "/download" )
	public String export( ExportConfig config, Model model, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			Exam exam = examService.getExamById( config.getExamId() ).orElse( null );
			if ( exam == null || exam.getState() != ExamState.Recorded ) {
				return "redirect:/recorded";
			}
			configRepo.save( config );
			model.addAttribute( "countOngoing", examService.countExamsByState( ExamState.Ongoing ) );
			model.addAttribute( "exam", exam );
			return "dashboard/download";
		}
		return "redirect:/exam";
	}

	/**
	 * Downloads a generated .csv file on the server to the client
	 * @param examId The examId of the exam to be exported
	 * @param request Used to determine if the request comes from the server
	 * @return A .csv file that is representing the exam on the database
	 */
	@ResponseBody
	@GetMapping( "/download/{examId}" )
	public FileSystemResource download( @PathVariable( "examId" ) Long examId, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			Exam exam = examService.getExamById( examId ).orElse( null );
			if ( exam == null || exam.getState() != ExamState.Recorded ) {
				return null;
			}
			try {
				ExportConfig config = configRepo.findById( ( byte )1 ).orElse( new ExportConfig() );
				String result = config.export( exam );
				File file = new File( config.getFilename() );
				file.deleteOnExit();
				if ( file.createNewFile() ) {
					FileWriter writer = new FileWriter( file, false );
					writer.write( result );
					writer.close();
				} else { // Failed to create a new file therefore file already exists
					FileWriter writer = new FileWriter( file, false );
					writer.write( result );
					writer.close();
				}
				return new FileSystemResource( file );
			} catch ( IOException e ) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * TODO
	 * @param examId
	 * @param model
	 * @param request
	 * @return
	 */
	@GetMapping( "/copy/{examId}" )
	public String copy( @PathVariable( "examId") Long examId, Model model, HttpServletRequest request ) {
		// TODO implement
		if ( isLocalhost( request ) ) {
			Exam exam = examService.getExamById( examId ).orElse( null );
			if ( exam == null || exam.getState() != ExamState.Recorded ) {
				return "redirect:/exam";
			}
			// CopyExamDTO examDTO = new CopyExamDTO( exam );
			// model.addAttribute( "exam", examDTO );
			return "dashboard/modify-scheculed";
		}
		return "redirect:/exam";
	}

	/**
	 * Deletes the exam based on the {@code examId}
	 * @param examId The id of the exam to be deleted
	 * @param request Used to determine if request comes from the server
	 * @return A redirect back to either the recorded page if the previous page was the recorded page or else the scheduled page
	 */
	@GetMapping( "/delete/{examId}" )
	public String deleteExam( @PathVariable( "examId" ) Long examId, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			examService.deleteExamById( examId );
			String referer = request.getHeader( "referer" );
			if ( referer.contains( "recorded" ) ) {
				return "redirect:/recorded";
			}
			return "redirect:/scheduled";
		}
		return "redirect:/exam";
	}

	/**
	 * Stops the exam if the exam is meant to be or is currently ongoing. Could potentially update the
	 * end time.
	 * @param examId The id of the exam to be stopped
	 * @param request Used to determine if request comes from the server
	 * @return A redirect to /recorded if the exam is recorded
	 * @see ExamService
	 */
	@GetMapping( "/stop/{examId}" )
	public String stopExam( @PathVariable( "examId" ) Long examId, HttpServletRequest request ) {
		if ( isLocalhost( request ) ) {
			examService.stopExamById( examId );
			Exam exam = examService.getExamById( examId ).orElse( null );
			if ( exam == null || exam.getState() != ExamState.Recorded ) {
				return "redirect:/scheduled";
			}
			return "redirect:/recorded";
		}
		return "redirect:/exam";
	}
}
