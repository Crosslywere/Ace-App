package com.ace.app.service;

import java.util.List;
import java.util.Optional;

import com.ace.app.dto.CreateExamDTO;
import com.ace.app.dto.ModifyOExamDTO;
import com.ace.app.dto.ModifySExamDTO;
import com.ace.app.entity.Exam;
import com.ace.app.model.ExamState;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
public interface ExamService {

	/**
	 * Updates all exam's states based on the current time and date
	 */
	void sortExams();

	/**
	 * @return A list of all exams currently stored on the server's database
	 */
	List<Exam> getAllExams();

	/**
	 * 
	 * @param state The current state of the exams expected from the return 
	 * @return A list of all exams with the {@code state} as their current state on the database
	 */
	List<Exam> getExamsByState( ExamState state);

	/**
	 * Gets an exam that may or maynot exist via the {@code examId}
	 * @param examId
	 * @return Optional object that may contain an exam or {@code null}
	 */
	Optional<Exam> getExamById( Long examId );

	// /**
	//  * 
	//  * @param title
	//  * @return
	//  */
	// List<Exam> getExamsByTitleLike( String title );

	/**
	 * Gets the number of exams with their current state as the argument
	 * @param state The current state of the exams expected from the return
	 * @return The number of exams with the {@code state} as their current state on the database
	 */
	long countExamsByState( ExamState state );

	/**
	 * Creates an exam based on the data in the data transfer object
	 * @param examDTO Data transfer object to create the exam from
	 * @return {@code true} if the exam was successfully created, otherwise {@code false}
	 */
	void createExam( CreateExamDTO examDTO );

	/**
	 * Deletes an exam via its exam id.
	 * <p>This also cascades and deletes everything related to the exam including papers and candidates</p>
	 * @param examId The id of the exam to be deleted
	 */
	void deleteExamById( Long examId );

	/**
	 * Stops the exam with the corresponding examId if and only if the exam is meant to be ongoing
	 * @apiNote Can also modify the exam's endTime if the exam is stopped prematurly
	 * @param examId The id of the exam to be stopped
	 */
	void stopExamById( Long examId );

	/**
	 * TODO describe
	 * @param examDTO
	 */
	void update( ModifySExamDTO examDTO );

	/**
	 * TODO describe
	 * @param examDTO
	 */
	void update( ModifyOExamDTO examDTO );
}
