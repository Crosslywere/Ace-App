package com.ace.app.service.impl;

import java.sql.Time;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ace.app.dto.CreateCandidateDTO;
import com.ace.app.dto.CreateExamDTO;
import com.ace.app.entity.Candidate;
import com.ace.app.model.CandidateField;
import com.ace.app.service.NotificationSenderService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jude Ogboru
 * @version 0.0.1-SNAPSHOT
 */
@Async
@Service
@Slf4j
public class NotificationSenderServiceImpl implements NotificationSenderService {
	@Autowired
	private JavaMailSender mailSender;

	@Value( "${spring.mail.username}" )
	private String fromMail;

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
	public void sendMail( Candidate candidate ) {
		if ( candidate == null || candidate.getEmail() == null ) {
			return;
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
		} catch ( Exception e ) {
			log.error( e.getMessage() );
		}
	}

	@Override
	public <C extends CreateCandidateDTO, E extends CreateExamDTO> void sendMail( C candidateDTO, E examDTO ) {
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
			}
		}
	}

}
