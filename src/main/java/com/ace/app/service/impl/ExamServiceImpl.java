package com.ace.app.service.impl;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ace.app.dto.CreateCandidateDTO;
import com.ace.app.dto.CreateExamDTO;
import com.ace.app.dto.ModifyOExamDTO;
import com.ace.app.dto.ModifySExamDTO;
import com.ace.app.entity.Candidate;
import com.ace.app.entity.Exam;
import com.ace.app.model.CandidateField;
import com.ace.app.model.ExamState;
import com.ace.app.model.ModifyTodo;
import com.ace.app.repository.ExamRepository;
import com.ace.app.service.ExamService;
import com.ace.app.service.NotificationSenderService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Service
@Slf4j
public class ExamServiceImpl implements ExamService {

	@Autowired
	private ExamRepository examRepository;

	@Autowired
	private NotificationSenderService senderService;

	private static Date today = null;
	
	private static Time now = null;

	private static List<Exam> examsToUpdate = new ArrayList<>();

	/**
	 * Updates the exam state of exam ie should be Ongoing or Recorded.
	 * If updated is stored in the static class variable {@code examsToUpdate}
	 * @param exam The exam to be updated
	 */
	private void updateExamState( Exam exam ) {
		if ( exam == null || exam.getState() == ExamState.Recorded ) return;
		if ( today == null || now == null ) {
			today = Date.valueOf( new Date( System.currentTimeMillis() ).toString() );
			now = Time.valueOf( new Time( System.currentTimeMillis() ).toString() );
		}
		if ( ( exam.getScheduledDate().compareTo( today ) == 0 && ( exam.getStartTime().compareTo( now ) <= 0
				&& exam.getEndTime().compareTo( now ) > 0 ) ) && exam.getState() != ExamState.Ongoing ) {
			if ( !exam.getCandidates().isEmpty() ) {
				exam.getCandidates().forEach( candidate -> {
					exam.getPapers().forEach( paper -> {
						if ( paper.getManditory() && !candidate.getPapernames().contains( paper.getName() ) ) {
							candidate.getPapernames().add( paper.getName() );
						}
					} );
				} );
				exam.setState( ExamState.Ongoing );
				examsToUpdate.add( exam );
			}
		} else if ( exam.getScheduledDate().compareTo( today ) < 0 || ( exam.getScheduledDate().compareTo( today ) == 0
				&& exam.getEndTime().compareTo( now ) <= 0 ) ) {
			exam.setState( ExamState.Recorded );
			for ( Candidate candidate : exam.getCandidates() ) {
				if ( candidate.getHasLoggedIn() ) {
					candidate.setSubmitted( true );
					Candidate.score( candidate );
				}
			}
			examsToUpdate.add( exam );
		}
	}

	/**
	 * Resets all of this class' static variables
	 */
	private void cleanup() {
		examsToUpdate = new ArrayList<>();
		today = null;
		now = null;
	}

	@Override
	public void sortExams() {
		List<Exam> exams = examRepository.findAll();
		exams.forEach( this::updateExamState );
		if ( !examsToUpdate.isEmpty() ) {
			examRepository.saveAll( examsToUpdate );
		}
		cleanup();
	}

	@Override
	public List<Exam> getAllExams() {
		return examRepository.findAll();
	}

	@Override
	public List<Exam> getExamsByState( ExamState state ) {
		return examRepository.findByState( state );
	}

	@Override
	public Optional<Exam> getExamById( Long examId ) {
		if ( examId == null ) {
			return Optional.empty();
		}
		return examRepository.findById( examId );
	}

	@Override
	public long countExamsByState( ExamState state ) {
		return examRepository.countByState( state );
	}

	@Async
	@Override
	public void createExam( CreateExamDTO examDTO ) {
		if ( examDTO.getExamId() != null ) {
			// The update exam method should be called instead of the create exam method
			// Because this will update an already existing exam on the database
			// Or could otherwise be overwritten causing an error
			log.warn( "Use update exam. Attempted to create exam with exam Id = {}", examDTO.getExamId() );
			// System.out.println( "Use update exam. Attempted to create exam with exam Id = " + examDTO.getExamId() );
			return;
		}
		examRepository.save( new Exam( examDTO ) );
		for ( CreateCandidateDTO candidate : examDTO.getCandidates() ) {
			// Remove the extra login field that is not desired
			if ( examDTO.getLoginField2() == CandidateField.None ) {
				candidate.setField2( "" );
			}
			// Sending notifying email
			if ( candidate.getEmail() != null && !candidate.getEmail().isBlank() && examDTO.isSendEmail() ) {
				senderService.sendMail( candidate, examDTO );
			}
		}
	}

	@Override
	public void deleteExamById( Long examId ) {
		examRepository.deleteById( examId );
	}

	@Override
	public void stopExamById( Long examId ) {
		if ( examId != null ) {
			Exam exam = examRepository.findById( examId ).orElse( null );
			if ( exam != null ) {
				today = Date.valueOf( new Date( System.currentTimeMillis() ).toString() );
				now = Time.valueOf( new Time( System.currentTimeMillis() ).toString() );
				if ( exam.getScheduledDate().compareTo( today ) == 0 && exam.getEndTime().compareTo( now ) > 0 ) {
					exam.setEndTime( now );
				}
				updateExamState( exam );
				examRepository.saveAll( examsToUpdate );
				cleanup();
			}
		}
	}

	@Override
	public void update( ModifySExamDTO examDTO ) {
		if ( examDTO.getExamId() == null ) {
			return;
		}
		Exam oldExam = examRepository.findById( examDTO.getExamId() ).orElse( null );
		if ( oldExam != null ) {
			final BooleanWrapper timeChanged = new BooleanWrapper();
			boolean shouldSave = false;
			Exam replacement = new Exam( examDTO );
			today = Date.valueOf( new Date( System.currentTimeMillis() ).toString() );
			if ( !replacement.getTitle().equals( oldExam.getTitle() ) ) {
				oldExam.setTitle( replacement.getTitle() );
				shouldSave = true;
			}
			if ( !replacement.getScheduledDate().equals( oldExam.getScheduledDate() ) && replacement.getScheduledDate().compareTo( today ) > -1 ) {
				oldExam.setScheduledDate( replacement.getScheduledDate() );
				timeChanged.setValue( true );
				shouldSave = true;
			}
			if ( !replacement.getStartTime().equals( oldExam.getStartTime() ) ) {
				oldExam.setStartTime( replacement.getStartTime() );
				timeChanged.setValue( true );
				shouldSave = true;
			}
			if ( !replacement.getEndTime().equals( oldExam.getEndTime() ) ) {
				oldExam.setEndTime( replacement.getEndTime() );
				timeChanged.setValue( true );
				shouldSave = true;
			}
			updateExamState( oldExam );
			cleanup();
			if ( oldExam.getState() != ExamState.Scheduled ) {
				shouldSave = true;
			}
			if ( replacement.getDuration() != oldExam.getDuration() ) {
				oldExam.setDuration( replacement.getDuration() );
				shouldSave = true;
			}
			if ( replacement.getAllowCutOffMark() != oldExam.getAllowCutOffMark() ) {
				oldExam.setAllowCutOffMark( replacement.getAllowCutOffMark() );
				shouldSave = true;
			}
			if ( oldExam.getAllowCutOffMark() && replacement.getCutOffMark() != oldExam.getCutOffMark() ) {
				oldExam.setCutOffMark( replacement.getCutOffMark() );
				shouldSave = true;
			}
			if ( oldExam.getShowResult() != replacement.getShowResult() ) {
				oldExam.setShowResult( replacement.getShowResult() );
				shouldSave = true;
			}
			if ( !replacement.getLoginField1().equals( oldExam.getLoginField1() ) ) {
				oldExam.setLoginField1( replacement.getLoginField1() );
				shouldSave = true;
			}
			if ( !replacement.getLoginField1Desc().equals( oldExam.getLoginField1Desc() ) ) {
				oldExam.setLoginField1Desc( replacement.getLoginField1Desc() );
				shouldSave = true;
			}
			if ( !replacement.getLoginField2().equals( oldExam.getLoginField2() ) ) {
				oldExam.setLoginField2( replacement.getLoginField2() );
				shouldSave = true;
			}
			if ( !replacement.getLoginField2Desc().equals( oldExam.getLoginField2Desc() ) ) {
				oldExam.setLoginField2Desc( replacement.getLoginField2Desc() );
				shouldSave = true;
			}
			if ( replacement.getPapersPerCandidate() != oldExam.getPapersPerCandidate() ) {
				oldExam.setPapersPerCandidate( replacement.getPapersPerCandidate() );
				shouldSave = true;
			}
			if ( examDTO.getPaperTodo() == ModifyTodo.Replace && !replacement.getPapers().isEmpty() ) {
				oldExam.getPapers().clear();
				examRepository.save( oldExam );
				oldExam.getPapers().addAll( replacement.getPapers() );
				shouldSave = true;
			} else if ( examDTO.getPaperTodo() != ModifyTodo.Ignore ) {
				replacement.getPapers().forEach( paper -> {
					boolean handled = false;
					for ( int i = 0; i < oldExam.getPapers().size(); i++ ) {
						if ( oldExam.getPapers().get( i ).getName().equalsIgnoreCase( paper.getName() ) ) {
							oldExam.getPapers().get( i ).append( paper.getQuestions() );
							oldExam.getPapers().get( i ).setManditory( paper.getManditory() );
							handled = true;
							break;
						}
					}
					if ( !handled ) {
						oldExam.getPapers().add( paper );
					}
				} );
				shouldSave = true;
			}
			if ( examDTO.getCandidateTodo() != ModifyTodo.Ignore ) {
				if ( examDTO.getCandidateTodo() == ModifyTodo.Replace ) {
					oldExam.getCandidates().clear();
				}
				replacement.getCandidates().forEach( candidate -> {
					boolean handled = false;
					boolean notificationHandled = false;
					for ( int i = 0; i < oldExam.getCandidates().size(); i++ ) {
						if ( oldExam.getCandidates().get( i ).getField1().equals( candidate.getField1() ) ) {
							if ( !candidate.getEmail().equals( oldExam.getCandidates().get( i ).getEmail() ) || 
								 !candidate.getPhoneNumber().equals( oldExam.getCandidates().get( i ).getPhoneNumber() ) ||
								 !timeChanged.getValue() )  {
								senderService.sendMail( candidate );
								notificationHandled = true;
							}
							oldExam.getCandidates().set( i, candidate );
							handled = true;
							break;
						}
					}
					if ( !handled ) {
						oldExam.getCandidates().add( candidate );
					}
					if ( !notificationHandled ) {
						senderService.sendMail( candidate );
					}
				} );
				shouldSave = true;
			}
			if ( shouldSave ) {
				examRepository.save( oldExam );
			}
		}
	}

	@Override
	public void update( ModifyOExamDTO examDTO ) {
		if ( examDTO.getExamId() == null ) {
			return;
		}
		Exam oldExam = examRepository.findById( examDTO.getExamId() ).orElse( null );
		if ( oldExam != null ) {
			Exam replacement = new Exam( examDTO );
			if ( !replacement.getTitle().equals( oldExam.getTitle() ) ) {
				oldExam.setTitle( replacement.getTitle() );
			}
			if ( replacement.getEndTime().compareTo( oldExam.getEndTime() ) != 0 ) {
				oldExam.setEndTime( replacement.getEndTime() );
			}
			if ( replacement.getAllowCutOffMark() != oldExam.getAllowCutOffMark() ) {
				oldExam.setAllowCutOffMark( replacement.getAllowCutOffMark() );
			}
			if ( oldExam.getAllowCutOffMark() && replacement.getCutOffMark() != oldExam.getCutOffMark() ) {
				oldExam.setCutOffMark( replacement.getCutOffMark() );
			}
			if ( oldExam.getShowResult() != replacement.getShowResult() ) {
				oldExam.setShowResult( replacement.getShowResult() );
			}
			if ( !replacement.getLoginField1().equals( oldExam.getLoginField1() ) ) {
				oldExam.setLoginField1( replacement.getLoginField1() );
			}
			if ( !replacement.getLoginField1Desc().equals( oldExam.getLoginField1Desc() ) ) {
				oldExam.setLoginField1Desc( replacement.getLoginField1Desc() );
			}
			if ( !replacement.getLoginField2().equals( oldExam.getLoginField2() ) ) {
				oldExam.setLoginField2( replacement.getLoginField2() );
			}
			if ( !replacement.getLoginField2Desc().equals( oldExam.getLoginField2Desc() ) ) {
				oldExam.setLoginField2Desc( replacement.getLoginField2Desc() );
			}
			replacement.getCandidates().forEach( candidate -> {
				for ( Candidate oldCandidate : oldExam.getCandidates() ) {
					if ( oldCandidate.getField1().equals( candidate.getField1() ) && oldCandidate.getField2().equals( candidate.getField2() ) ) {
						if ( !oldCandidate.getSubmitted() && candidate.getHasLoggedIn() != null ) {
							oldCandidate.setHasLoggedIn( candidate.getHasLoggedIn() );
						}
						break;
					} 
				}
			} );
			updateExamState( oldExam );
			cleanup();
			examRepository.save( oldExam );
		}
	}

	// Helper class that is needed to make booleans final/static while being modifiable or scoped to a none static method
	private class BooleanWrapper {
		private boolean value = false;

		public boolean getValue() {
			return value;
		}

		public void setValue( boolean value ) {
			this.value = value;
		}
	}
}
