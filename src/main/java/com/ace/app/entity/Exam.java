package com.ace.app.entity;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.ace.app.dto.CreateExamDTO;
import com.ace.app.dto.ModifyOExamDTO;
import com.ace.app.dto.ModifySExamDTO;
import com.ace.app.model.CandidateField;
import com.ace.app.model.ExamState;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 21-June-2024
 */
@Entity
@Getter
@Setter
@Table( name = "exams" )
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private Long examId;

	@Column( nullable = false )
	private String title;

	@Column( nullable = false )
	private Date scheduledDate;

	@Column( nullable = false )
	private Time startTime;

	@Column( nullable = false )
	private Time endTime;

	@Column( nullable = false )
	private Integer duration;

	@Column( nullable = false )
	private Float cutOffMark;

	private Boolean allowCutOffMark = false;

	@Column( nullable = false )
	private ExamState state;

	private Boolean showResult = true;
	
	@Column( nullable = false )
	private Integer papersPerCandidate = 1;

	private Boolean registrationLocked = false;

	@Column( nullable = false )
	@Enumerated( EnumType.ORDINAL )
	private CandidateField loginField1;

	@Column( nullable = false, length = 50 )
	private String loginField1Desc;

	@Column( nullable = false )
	@Enumerated( EnumType.ORDINAL )
	private CandidateField loginField2;

	@Column( length = 50 )
	private String loginField2Desc;

	@OneToMany( cascade = CascadeType.ALL, mappedBy = "paperId.exam", orphanRemoval = true )
	private List<Paper> papers;

	@OneToMany( cascade = CascadeType.ALL, mappedBy = "candidateId.exam", orphanRemoval = true )
	private List<Candidate> candidates;

	public Exam( CreateExamDTO examDTO ) {
		examId = examDTO.getExamId();
		title = examDTO.getTitle();
		scheduledDate = examDTO.getScheduledDate();
		startTime = Time.valueOf( examDTO.getStartTime() + ":00" );
		endTime = Time.valueOf( examDTO.getEndTime() + ":00" );
		duration = examDTO.getDuration();
		cutOffMark = examDTO.getCutOffMark();
		allowCutOffMark = examDTO.getAllowCutOffMark();
		state = ExamState.Scheduled;
		showResult = examDTO.getShowResult();
		papersPerCandidate = examDTO.getPapersPerCandidate();
		loginField1 = examDTO.getLoginField1();
		loginField1Desc = examDTO.getLoginField1Desc();
		loginField2 = examDTO.getLoginField2();
		loginField2Desc = examDTO.getLoginField2Desc();
		papers = new ArrayList<>();
		examDTO.getPapers().forEach( paperDTO -> {
			papers.add( new Paper( paperDTO, this ) );
		} );
		candidates = new ArrayList<>();
		examDTO.getCandidates().forEach( candidateDTO -> {
			candidates.add( new Candidate( candidateDTO, this ) );
		} );
		registrationLocked = !candidates.isEmpty();
	}

	public Exam( ModifySExamDTO examDTO ) {
		examId = examDTO.getExamId();
		title = examDTO.getTitle();
		scheduledDate = examDTO.getScheduledDate();
		startTime = Time.valueOf( examDTO.getStartTime() + ":00" );
		endTime = Time.valueOf( examDTO.getEndTime() + ":00" );
		duration = examDTO.getDuration();
		cutOffMark = examDTO.getCutOffMark();
		allowCutOffMark = examDTO.getAllowCutOffMark();
		state = ExamState.Scheduled;
		showResult = examDTO.getShowResult();
		papersPerCandidate = examDTO.getPapersPerCandidate();
		loginField1 = examDTO.getLoginField1();
		loginField1Desc = examDTO.getLoginField1Desc();
		loginField2 = examDTO.getLoginField2();
		loginField2Desc = examDTO.getLoginField2Desc();
		papers = new ArrayList<>();
		// Adding all the papers be it modified or created
		examDTO.getPapers().forEach( paperDTO -> {
			papers.add( new Paper( paperDTO, this ) );
		} );
		examDTO.getModifyPapers().forEach( paperDTO -> {
			papers.add( new Paper( paperDTO, this ) );
		} );
		candidates = new ArrayList<>();
		// Adding all the candidates be it modified or created
		examDTO.getCandidates().forEach( candidateDTO -> {
			candidates.add( new Candidate( candidateDTO, this ) );
		} );
		examDTO.getModifyCandidates().forEach( candidateDTO -> {
			candidates.add( new Candidate( candidateDTO, this ) );
		} );
	}

	public Exam( ModifyOExamDTO examDTO ) {
		examId = examDTO.getExamId();
		title = examDTO.getTitle();
		endTime = Time.valueOf( examDTO.getEndTime() + ":00" );
		state = ExamState.Ongoing;
		cutOffMark = examDTO.getCutOffMark();
		showResult = examDTO.getShowResult();
		loginField1 = examDTO.getLoginField1();
		loginField1Desc = examDTO.getLoginField1Desc();
		loginField2 = examDTO.getLoginField2();
		loginField2Desc = examDTO.getLoginField2Desc();
		registrationLocked = examDTO.getRegistrationLocked();
		candidates = new ArrayList<>();
		examDTO.getCandidates().forEach( candidateDTO -> {
			candidates.add( new Candidate( candidateDTO, this ) );
		} );
	}

	public List<Candidate> getCandidatesSubmitted() {
		List<Candidate> candidatesSubmitted = new ArrayList<>();
		candidates.forEach( candidate -> {
			if ( candidate.getSubmitted() ) {
				candidatesSubmitted.add( candidate );
			}
		} );
		return candidatesSubmitted;
	}
}
