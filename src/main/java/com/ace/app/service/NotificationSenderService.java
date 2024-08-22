package com.ace.app.service;

import com.ace.app.dto.CreateCandidateDTO;
import com.ace.app.dto.CreateExamDTO;
import com.ace.app.entity.Candidate;

public interface NotificationSenderService {

	/**
	 * Sends an email with the parameters specified
	 * @param to The email address of the reciever.
	 * @param subject The subject of the email.
	 * @param body The body of the email.
	 */
	void sendMail( String to, String subject, String body );

	/**
	 * Sends an SMS with the parameters specified
	 * @param to The phone number of the reciever.
	 * @param body The body of the SMS.
	 */
	void sendSMS( String to, String body );
	
	/**
	 * Sends an email to the candidate if the candidate has an email
	 * @param candidate The candidate to send the email to.
	 * @return {@code true} on success, {@code false} otherwise. Email sending may still fail
	 * if the email isn't valid in such instances the function may return {@code true} but 
	 * send the failure to the sending email.
	 */
	boolean sendMail( Candidate candidate );

	/**
	 * Sends an SMS to the candidate if the candidate has a phone number
	 * @param candidate The candidate to send the SMS to.
	 * @return {@code true} on success, {@code false} otherwise.
	 */
	boolean sendSMS( Candidate candidate );

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
	<C extends CreateCandidateDTO, E extends CreateExamDTO> boolean sendMail( C candidateDTO, E examDTO );

	/**
	 * Sends an SMS to the candidate if the candidate has a phone number
	 * @param <C> A class that extends the {@code CreateCandidateDTO}
	 * @param <E> A class that extends the {@code CreateExamDTO}
	 * @param candidateDTO The candidate to send the email to.
	 * @param examDTO The exam's information
	 * @return {@code true} on success, {@code false} otherwise.
	 */
	<C extends CreateCandidateDTO, E extends CreateExamDTO> boolean sendSMS( C candidateDTO, E examDTO );
}