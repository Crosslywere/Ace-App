package com.ace.app.service.impl;

import java.sql.Time;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ace.app.dto.CreateCandidateDTO;
import com.ace.app.dto.CreateExamDTO;
import com.ace.app.entity.Candidate;
import com.ace.app.model.CandidateField;
import com.ace.app.service.NotificationSenderService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jude Ogboru
 * @version 0.0.1-SNAPSHOT
 */
@Service
@Slf4j
public class NotificationSenderServiceImpl implements NotificationSenderService {
	@Autowired
	private JavaMailSender mailSender;

	@Value( "${spring.mail.username}" )
	private String fromMail;
	
	@Value( "${twilio.number}" )
	private String twilioNumber;

	@Value( "${twilio.sid}" )
	private String twilioSID;

	@Value( "${twilio.auth}" )
	private String twilioAuth;

	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "d MMMMM, yyyy" );

	private final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat( "h:mm aa" );

	@Override
	public void sendMail( String toEmail, String subject, String body ) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom( fromMail );
		message.setTo( toEmail );
		message.setSubject( subject );
		message.setText( body );

		mailSender.send( message );
	}

	@Override
	public void sendSMS( String toNumber, String body ) {
		Twilio.init( twilioSID, twilioAuth );
		Message.creator( new PhoneNumber( toNumber ), new PhoneNumber( twilioNumber ), body ).create();
		// Twilio.destroy();
	}

	@Override
	public boolean sendMail( Candidate candidate ) {
		if ( candidate == null || candidate.getEmail() == null ) {
			return false;
		}
		String toEmail = candidate.getEmail();
		String subject = "Ace Training Center " + candidate.getExam().getTitle() + " Exam";
		String body = "Your login information is\n"
			+ candidate.getExam().getLoginField1Desc() + ":" + candidate.getField1() + "\n";
		if ( candidate.getExam().getLoginField2() != CandidateField.None ) {
			body += candidate.getExam().getLoginField2Desc() + ":" + candidate.getField2() + "\n";
		}
		body += "Date:" + DATE_FORMAT.format( candidate.getExam().getScheduledDate() ) + "\n";
		body += "Exam Starts:" + TIME_FORMAT.format( candidate.getExam().getStartTime() ) + "\n";
		body += "Exam Ends:" + TIME_FORMAT.format( candidate.getExam().getEndTime() );
		try {
			sendMail( toEmail, subject, body );
			return true;
		} catch ( Exception e ) {
			log.error( e.getMessage() );
			return false;
		}
	}

	@Override
	public boolean sendSMS( Candidate toCandidate ) {
		if ( toCandidate == null || toCandidate.getPhoneNumber() == null || toCandidate.getPhoneNumber().isBlank() ) {
			return false;
		}
		String toNumber = localizePhoneNumber( toCandidate.getPhoneNumber() );
		String body = "Ace Training Center\n" 
			+ "Your login information is\n"
			+ toCandidate.getExam().getLoginField1Desc() + ":" + toCandidate.getField1() + "\n";
		if ( toCandidate.getExam().getLoginField2() != CandidateField.None ) {
			body += toCandidate.getExam().getLoginField2Desc() + ":" + toCandidate.getField2() + "\n";
		}
		body += "Date:" + DATE_FORMAT.format( toCandidate.getExam().getScheduledDate() ) + "\n";
		body += "Exam Starts:" + TIME_FORMAT.format( toCandidate.getExam().getStartTime() ) + "\n";
		body += "Exam Ends:" + TIME_FORMAT.format( toCandidate.getExam().getEndTime() );
		try {
			sendSMS( toNumber, body );
			return true;
		} catch ( Exception e ) {
			log.error( e.getMessage() );
			return false;
		}
	}

	@Override
	public <C extends CreateCandidateDTO, E extends CreateExamDTO> boolean sendMail( C candidateDTO, E examDTO ) {
		if ( candidateDTO != null && !candidateDTO.getEmail().isBlank() ) {
			String to = candidateDTO.getEmail();
			String subject = "Ace Training Center " + examDTO.getTitle() + " Exam";
			String body = "Your login information is\n" + examDTO.getLoginField1Desc() + ":" + candidateDTO.getField1() + "\n";
			if ( examDTO.getLoginField2() != CandidateField.None ) {
				body += examDTO.getLoginField2Desc() + ":" + candidateDTO.getField2() + "\n";
			}
			body += "Date:" + DATE_FORMAT.format( examDTO.getScheduledDate() ) + "\n";
			body += "Exam Starts:" + TIME_FORMAT.format( Time.valueOf( examDTO.getStartTime() + ":00" ) ) + "\n";
			body += "Exam Ends:" + TIME_FORMAT.format( Time.valueOf( examDTO.getEndTime() + ":00" ) );
			try {
				sendMail( to, subject, body );
			} catch ( Exception e ) {
				log.error( e.getMessage() );
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public <C extends CreateCandidateDTO, E extends CreateExamDTO> boolean sendSMS( C candidateDTO, E examDTO ) {
		if ( candidateDTO != null && candidateDTO.getPhoneNumber() != null && !candidateDTO.getPhoneNumber().isBlank() ) {
			String to = localizePhoneNumber( candidateDTO.getPhoneNumber() );
			String body = "Ace Training Center\n" 
				+ "Your login information is\n"
				+ examDTO.getLoginField1Desc() + ":" + candidateDTO.getField1() + "\n";
			if ( examDTO.getLoginField2() != CandidateField.None ) {
				body += examDTO.getLoginField2Desc() + ":" + candidateDTO.getField2() + "\n";
			}
			body += "Date:" + DATE_FORMAT.format( examDTO.getScheduledDate() ) + "\n";
			body += "Exam Starts:" + TIME_FORMAT.format( Time.valueOf( examDTO.getStartTime() + ":00" ) ) + "\n";
			body += "Exam Ends:" + TIME_FORMAT.format( Time.valueOf( examDTO.getEndTime() + ":00" ) );
			try {
				sendSMS( to, body );
			} catch ( Exception e ) {
				log.error( e.getMessage() );
				return false;
			}
			return true;
		}
		return false;
	}

	private static String localizePhoneNumber( String phoneNumber ) {
		if ( phoneNumber.startsWith( "+234" ) ) {
			return phoneNumber;
		} else {
			return phoneNumber.replaceAll( "^0", "+234" );
		}
	}
}
