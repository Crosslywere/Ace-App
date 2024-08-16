package com.ace.app.service.impl;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Service
public class ExamServiceImpl implements ExamService {

	@Autowired
	private ExamRepository examRepository;

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
			exam.setState( ExamState.Ongoing );
			examsToUpdate.add( exam );
		} else if ( exam.getScheduledDate().compareTo( today ) < 0 || ( exam.getScheduledDate().compareTo( today ) == 0
				&& exam.getEndTime().compareTo( now ) <= 0 ) ) {
			exam.setState( ExamState.Recorded );
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

	public List<Exam> getExamsByTitleLike( String title ) {
		return examRepository.findByTitleContainingIgnoreCase( title );
	}

	@Override
	public long countExamsByState( ExamState state ) {
		return examRepository.countByState( state );
	}

	@Override
	public boolean createExam( CreateExamDTO examDTO ) {
		if ( examDTO.getExamId() != null ) {
			// The update exam method should be called instead of the create exam method
			// Because this will update an already existing exam on the database
			// Or could otherwise be overwritten causing an error
			System.out.println( "Use update exam. Attempted to create exam with exam Id = " + examDTO.getExamId() );
			return false;
		}
		// Remove the extra login field that is not desired
		if ( examDTO.getLoginField2() == CandidateField.None ) {
			for ( var candidate : examDTO.getCandidates() ) {
				candidate.setField2( "" );
			}
		}
		examRepository.save( new Exam( examDTO ) );
		return true;
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
			boolean shouldSave = false;
			Exam replacement = new Exam( examDTO );
			today = Date.valueOf( new Date( System.currentTimeMillis() ).toString() );
			if ( !replacement.getTitle().equals( oldExam.getTitle() ) ) {
				oldExam.setTitle( replacement.getTitle() );
				shouldSave = true;
			}
			if ( !replacement.getScheduledDate().equals( oldExam.getScheduledDate() ) && replacement.getScheduledDate().compareTo( today ) > -1 ) {
				oldExam.setScheduledDate( replacement.getScheduledDate() );
				shouldSave = true;
			}
			if ( !replacement.getStartTime().equals( oldExam.getStartTime() ) ) {
				oldExam.setStartTime( replacement.getStartTime() );
				shouldSave = true;
			}
			if ( !replacement.getEndTime().equals( oldExam.getEndTime() ) ) {
				oldExam.setEndTime( replacement.getEndTime() );
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
					for ( int i = 0; i < oldExam.getCandidates().size(); i++ ) {
						if ( oldExam.getCandidates().get( i ).getField1().equals( candidate.getField1() ) ) {
							oldExam.getCandidates().set( i, candidate );
							handled = true;
							break;
						}
					}
					if ( !handled ) {
						oldExam.getCandidates().add( candidate );
					}
				} );
				oldExam.setRegistrationLocked( !oldExam.getCandidates().isEmpty() );
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
			if ( replacement.getRegistrationLocked() != oldExam.getRegistrationLocked() ) {
				oldExam.setRegistrationLocked( replacement.getRegistrationLocked() );
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
}
