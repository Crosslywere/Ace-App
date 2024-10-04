package com.ace.app.service;

import org.springframework.scheduling.annotation.Async;

import com.ace.app.dto.CreateCandidateDTO;
import com.ace.app.dto.CreateExamDTO;
import com.ace.app.entity.Candidate;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
public interface NotificationSenderService {

	/**
	 * Sends an email with the parameters specified
	 * @param to The email address of the reciever.
	 * @param subject The subject of the email.
	 * @param body The body of the email.
	 */
	@Async
	void sendMail( String to, String subject, String body );
	
	/**
	 * Sends an email to the candidate if the candidate has an email
	 * @param candidate The candidate to send the email to.
	 * @return {@code true} on success, {@code false} otherwise. Email sending may still fail
	 * if the email isn't valid in such instances the function may return {@code true} but 
	 * send the failure to the sending email.
	 */
	@Async
	void sendMail( Candidate candidate );

	/**
	 * Sends an email to the candidate if the candidate has an email
	 * @param <C> A class that extends the {@code CreateCandidateDTO}
	 * @param <E> A class that extends the {@code CreateExamDTO}
	 * @param candidateDTO The candidate to send the email to.
	 * @param examDTO The exam's information
	 * @return {@code true} on success, {@code false} otherwise. Email sending may still fail
	 * if the email isn't valid in such instances the function may return {@code true} but 
	 * send the failure to the sending email.
	 */
	@Async
	<C extends CreateCandidateDTO, E extends CreateExamDTO> void sendMail( C candidateDTO, E examDTO );

}