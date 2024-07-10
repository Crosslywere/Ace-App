package com.ace.app.dto;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ace.app.entity.Exam;
import com.ace.app.model.ModifyTodo;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Getter
@Setter
@NoArgsConstructor
public final class ModifySExamDTO extends CreateExamDTO {

	private List<ModifySPaperDTO> modifyPapers = new ArrayList<>();
	private List<ModifySCandidateDTO> modifyCandidates = new ArrayList<>();
	private ModifyTodo paperTodo = ModifyTodo.Ignore;
	private ModifyTodo candidateTodo = ModifyTodo.Ignore;
	
	public ModifySExamDTO( Exam exam ) {
		super();
		super.examId = exam.getExamId();
		super.title = exam.getTitle();
		super.duration = exam.getDuration();
		super.startTime = exam.getStartTime().toString().replaceAll( ":\\d+$", "" );
		super.endTime = exam.getEndTime().toString().replaceFirst( ":\\d+$", "" );
		super.allowCutOffMark = exam.getAllowCutOffMark();
		super.papersPerCandidate = exam.getPapersPerCandidate();
		super.showResult = exam.getShowResult();
		super.loginField1 = exam.getLoginField1();
		super.loginField1Desc = exam.getLoginField1Desc();
		super.loginField2 = exam.getLoginField2();
		super.loginField2Desc = exam.getLoginField2Desc();
		this.modifyPapers = new ArrayList<>();
		exam.getPapers().forEach( paper -> {
			modifyPapers.add( new ModifySPaperDTO( paper ) );
		} );
		this.modifyCandidates = new ArrayList<>();
		exam.getCandidates().forEach( candidate -> {
			modifyCandidates.add( new ModifySCandidateDTO( candidate ) );
		} );
	}

	@Override
	public boolean parsePaperDocument() {
		if ( super.getPaperDocument() == null || super.getPaperDocument().isEmpty() ) {
			return paperTodo == ModifyTodo.Append || paperTodo == ModifyTodo.Replace;
		}
		InputStream stream = null;
		try {
			stream = super.getPaperDocument().getInputStream();
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
		if ( stream == null ) {
			return false;
		}
		Document document = new Document();
		document.loadFromStream( stream, FileFormat.Auto );
		Map<String, CreatePaperDTO> paperMap = parsePapers( document.getText(), this );
		document.close();
		for ( String key : paperMap.keySet() ) {
			paperMap.get( key ).setName( key );
			paperMap.get( key ).setQuestionsPerCandidate( paperMap.get( key ).getQuestions().size() );
		}
		// Based on the todo
		switch ( paperTodo ) {
			case Append -> {
				for ( String key : paperMap.keySet() ) {
					boolean handled = false;
					// Search the papers if it contains the papername(key)
					for ( int imp = 0; imp < modifyPapers.size(); imp++ ) {
						if ( modifyPapers.get( imp ).getName().equalsIgnoreCase( key ) ) {
							modifyPapers.get( imp ).appendAll( paperMap.get( key ).getQuestions() );
							handled = true;
							break;
						}
					}
					if ( !handled ) {
						CreatePaperDTO paperDTO = paperMap.get( key );
						paperDTO.setQuestionsPerCandidate( paperDTO.getQuestions().size() );
						modifyPapers.add( new ModifySPaperDTO( paperDTO ) );
					}
				}
			}
			case Replace -> {
				// Remove all the original papers
				modifyPapers = new ArrayList<>();
				for ( String key : paperMap.keySet() ) {
					CreatePaperDTO paperDTO = paperMap.get( key );
					modifyPapers.add( new ModifySPaperDTO( paperDTO ) );
				}
			}
			case Ignore -> {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings( "unlikely-arg-type" )
	@Override
	public boolean parseCandidateLoginInfoDocument() {
		if ( super.getCandidateLoginInfoDocument() == null || super.getCandidateLoginInfoDocument().isEmpty() ) {
			return false;
		}
		InputStream stream = null;
		try {
			stream = super.getCandidateLoginInfoDocument().getInputStream();
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
		if ( stream == null ) {
			return false;
		}
		Document document = new Document();
		document.loadFromStream( stream, FileFormat.Auto );
		List<CreateCandidateDTO> candidateList = parseCandidates( document.getText(), this );
		document.close();
		switch ( candidateTodo ) {
			case Append -> {
				for ( CreateCandidateDTO candidateDTO : candidateList ) {
					if ( modifyCandidates.contains( candidateDTO ) ) {
						int index = modifyCandidates.indexOf( candidateDTO );
						modifyCandidates.remove( index );
						modifyCandidates.add( index, new ModifySCandidateDTO( candidateDTO ) );
					} else {
						modifyCandidates.add( new ModifySCandidateDTO( candidateDTO ) );
					}
				}
			}
			case Replace -> {
				modifyCandidates.clear();
				candidateList.forEach( candidateDTO -> {
					modifyCandidates.add( new ModifySCandidateDTO( candidateDTO ) );
				} );
			}
			case Ignore -> {
				return false;
			}
		}
		return true;
	}
}
