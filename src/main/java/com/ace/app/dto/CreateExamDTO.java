package com.ace.app.dto;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.web.multipart.MultipartFile;

import com.ace.app.model.CandidateField;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ogboru Jude
 * @version 04-June-2024
 */
@Getter
@Setter
public class CreateExamDTO extends BaseExamDTO {

	private MultipartFile paperDocument;
	private MultipartFile candidateLoginInfoDocument;
	private List<CreatePaperDTO> papers;
	private List<CreateCandidateDTO> candidates;
	private final Date MIN_DATE = new Date( System.currentTimeMillis() );


	public CreateExamDTO() {
		super();
		super.examId = null;
		super.title = "";
		super.scheduledDate = Date.valueOf( new Date( System.currentTimeMillis() + 86400000 ).toString() );
		super.startTime = "09:00";
		super.endTime = "17:00";
		super.duration = 60;
		super.allowCutOffMark = false;
		super.loginField1 = CandidateField.Number;
		super.loginField2 = CandidateField.Email;
		papers = new ArrayList<>();
		candidates = new ArrayList<>();
	}

	/**
	 * Reads the exam paper multipart file and returns a boolean based on if there were any questions extracted from the multipart file
	 * @return {@code true} if the parsed document has extracted exam papers (with questions) from the document, otherwise {@code false}
	 */
	public boolean parsePaperDocument() {
		if ( paperDocument == null || paperDocument.isEmpty() ) {
			return false;
		}
		InputStream stream = null;
		try {
			stream = paperDocument.getInputStream();
		} catch ( IOException e) {
			e.printStackTrace();
		}
		if ( stream == null ) {
			return false;
		}
		Document document = new Document();
		document.loadFromStream( stream, FileFormat.Auto );
		Map<String, CreatePaperDTO> paperMap = parsePapers( document.getText(), this );
		document.close();
		papers = new ArrayList<>();
		for ( String key : paperMap.keySet() ) {
			CreatePaperDTO paperDTO = paperMap.get( key );
			if ( !paperDTO.getQuestions().isEmpty() ) {
				paperDTO.setName( key );
				paperDTO.setQuestionsPerCandidate( paperDTO.getQuestions().size() );
				papers.add( paperDTO );
			}
		}
		super.setPapersPerCandidate( papers.size() / 2 + 1 );
		if ( papers.size() == 1 ) {
			papers.get( 0 ).setManditory( true );
		}
		return !papers.isEmpty();
	}

	/**
	 * 
	 * @return
	 */
	public boolean parseCandidateLoginInfoDocument() {
		if ( candidateLoginInfoDocument == null || candidateLoginInfoDocument.isEmpty() ) {
			return false;
		}
		InputStream stream = null;
		try {
			stream = candidateLoginInfoDocument.getInputStream();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		if ( stream == null ) {
			return false;
		}
		Document document = new Document();
		document.loadFromStream( stream, FileFormat.Auto );
		candidates = parseCandidates( document.getText(), this );
		document.close();
		return !candidates.isEmpty();
	}

	/**
	 * 
	 * @param text The text gotten from the file stream.
	 * @param self The instance of the class to be used to set show result.
	 * @return
	 */
	protected static Map<String, CreatePaperDTO> parsePapers( String text, CreateExamDTO self ) {
		self.setShowResult( true );
		Map<String, CreatePaperDTO> paperMap = new LinkedHashMap<>();
		Scanner scn = new Scanner( text );
		String line = "";
		String currentPaperName = self.getTitle();
		CreatePaperDTO currentPaper = new CreatePaperDTO();
		boolean handled = true;
		boolean retry = false;
		while ( scn.hasNextLine() ) {
			if ( handled ) {
				handled = false;
				line = scn.nextLine().trim();
			}
			if ( line.contains( "Evaluation Warning: The document was created with Spire.Doc for JAVA." ) ) {
				// Ignore the line
				handled = true;
				continue;
			}
			if ( line.isBlank() ) {
				handled = true;
				continue;
			}
			// If the line is a new paper
			if ( line.matches( "^[A-Za-z\\s]+:$" ) && !handled ) {
				// If the current paper has questions
				if ( !currentPaper.getQuestions().isEmpty() ) {
					paperMap.put( currentPaperName, currentPaper );
				}
				// Reset paper
				currentPaper = new CreatePaperDTO();
				// Set the name of the new paper
				currentPaperName = line.replace( ":", " " ).trim();
				handled = true;
			}
			// If the line is a question.
			if ( line.matches( "^\\d+\\.\\s\\S[\\S\\s]+" ) && !handled ) {
				CreateQuestionDTO question = new CreateQuestionDTO();
				String[] lineParts = line.split( "\\.\\s", 2 );
				question.setNumber( Integer.parseInt( lineParts[0].trim() ) );
				question.setQuestion( lineParts[1].trim() );
				handled = true;
				// Option Loop
				while ( scn.hasNextLine() ) {
					if ( handled ) {
						handled = false;
						line = scn.nextLine().trim();
					} else {
						retry = true;
						// Put the paper into the into the list
						currentPaper.getQuestions().add( question );
						if ( question.getAnswerIndex() == null ) {
							self.setShowResult( false );
						}
						// Exit the option loop if line is unhandled
						break;
					}
					// If the line matches an option eg A. Orange
					if ( line.matches( "^[A-Ea-e]\\.\\s[\\S\\s]+" ) && !handled ) {
						handled = true;
						if ( question.getOptions() == null ) {
							question.setOptions( new ArrayList<>() );
						}
						lineParts = line.split( "\\.\\s", 2 );
						// Converts characters like A/a to 0, B/b to 1, etc
						int optionIndex = lineParts[0].trim().toLowerCase().charAt(0) - 'a';
						// If the option index is equal to the size of the options then
						if ( optionIndex == question.getOptions().size() ) {
							question.getOptions().add( lineParts[1].trim() );
						} else {
							// Print which character the option should be identified by eg Option indexed incorrect C ... (Should be B)
							System.out.println( "Option indexed incorrect " + ( char )( optionIndex + 'A' ) + " in question number " + question.getNumber() + "\n(Should be " + ( char )( question.getOptions().size() + 'A' ) + ')' );
							question.getOptions().add( lineParts[1].trim() );
						}
					}
					// If the line matches an answer eg Ans: e Or ans: a
					else if ( line.matches( "^[Aa]ns:[\\s]*[A-Ea-e]" ) && !handled ) {
						handled = true;
						int answerIndex = line.replaceAll( "^[Aa]ns:", "" ).trim().toLowerCase().charAt( 0 ) - 'a';
						question.setAnswerIndex( ( short )answerIndex );
					}
					// Catch any dangling question
					if ( !scn.hasNextLine() ) {
						currentPaper.getQuestions().add( question );
						if ( question.getAnswerIndex() == null ) {
							self.setShowResult( false );
						}
					}
				}
			}
			if ( retry ) {
				retry = false;
				continue;
			}
			handled = true;
		}
		if ( !currentPaper.getQuestions().isEmpty() ) {
			paperMap.put( currentPaperName, currentPaper );
		}
		scn.close();
		return paperMap;
	}

	/**
	 * 
	 * @param text The text gotten from the file stream.
	 * @param self The instance of the class to be used to set the login fields.
	 * @return 
	 */
	 protected static List<CreateCandidateDTO> parseCandidates( String text, CreateExamDTO self ) {
		Scanner scanner = new Scanner( text );
		List<CreateCandidateDTO> candidates = new ArrayList<>();
		boolean firstLine = true;
		boolean singleColumn = false;
		while ( scanner.hasNextLine() ) {
			String line = scanner.nextLine();
			if ( line.matches( "^Evaluation Warning: The document was created with Spire\\.Doc for JAVA\\.$" ) ) {
				continue;
			}
			if ( line.isBlank() ) {
				continue;
			}
			// Reading the header of the csv file
			if ( firstLine ) {
				firstLine = false;
				List<String> columnHeads = parseCSVLine( line );
				if ( columnHeads.isEmpty() ) {
					scanner.close();
					return candidates;
				}
				if ( columnHeads.size() == 1 ) {
					singleColumn = true;
					String str = columnHeads.get( 0 );
					if ( !str.isBlank() ) {
						self.setLoginField1Desc( str );
						// If the line contains mail
						if ( str.matches( "^[\\S\\s]*[Mm][Aa][Ii][Ll][\\S\\s]*$" ) ) {
							self.setLoginField1( CandidateField.Email );
						}
						// If the line contains tel or phone
						else if ( str.matches( "^[\\S\\s]*[Tt][Ee][Ll][\\S\\s]*$" ) || str.matches( "^[\\S\\s]*[Pp][Hh][Oo][Nn][Ee][\\S\\s]*$" ) ) {
							self.setLoginField1( CandidateField.Telephone );
						}
						// If the line contains num
						else if ( str.matches( "^[\\S\\s]*[Nn][Uu][Mm][\\S\\s]*$" ) ) {
							self.setLoginField1( CandidateField.Number );
						}
						else {
							self.setLoginField1( CandidateField.Text );
						}
					}
					self.setLoginField2( CandidateField.None );
					self.setLoginField2Desc( null );
				} else {
					String str1 = columnHeads.get( 0 ), str2 = columnHeads.get( 1 );
					if ( !str1.isBlank() ) {
						self.setLoginField1Desc( str1 );
						// If the line contains mail
						if ( str1.matches( "^[\\S\\s]*[Mm][Aa][Ii][Ll][\\S\\s]*$" ) ) {
							self.setLoginField1( CandidateField.Email );
						}
						// If the line contains tel or phone
						else if ( str1.matches( "^[\\S\\s]*[Tt][Ee][Ll][\\S\\s]*$" ) || str1.matches( "^[\\S\\s]*[Pp][Hh][Oo][Nn][Ee][\\S\\s]*$" ) ) {
							self.setLoginField1( CandidateField.Telephone );
						}
						// If the line contains num
						else if ( str1.matches( "^[\\S\\s]*[Nn][Uu][Mm][\\S\\s]*$" ) ) {
							self.setLoginField1( CandidateField.Number );
						}
						else {
							self.setLoginField1( CandidateField.Text );
						}
					}
					if ( !str2.isBlank() ) {
						self.setLoginField2Desc( str2 );
						// If the line contains mail
						if ( str2.matches( "^[\\S\\s]*[Mm][Aa][Ii][Ll][\\S\\s]*$" ) ) {
							self.setLoginField2( CandidateField.Email );
						}
						// If the line contains tel or phone
						else if ( str2.matches( "^[\\S\\s]*[Tt][Ee][Ll][\\S\\s]*$" ) || str2.matches( "^[\\S\\s]*[Pp][Hh][Oo][Nn][Ee][\\S\\s]*$" ) ) {
							self.setLoginField2( CandidateField.Telephone );
						}
						// If the line contains num
						else if ( str2.matches( "^[\\S\\s]*[Nn][Uu][Mm][\\S\\s]*$" ) ) {
							self.setLoginField2( CandidateField.Number );
						}
						else {
							self.setLoginField2( CandidateField.Password );
						}
					}
				}
				continue;
			} else {
				// Reading the body of the csv file
				if ( singleColumn ) {
					CreateCandidateDTO candidate = new CreateCandidateDTO();
					String str = line.replaceAll( "^\\\"", "" ).replaceAll( "\\\"\\s*[,]*\\s*$", "" ).trim();
					candidate.setField1( str );
					candidates.add( candidate );
				} else {
					CreateCandidateDTO candidate = new CreateCandidateDTO();
					List<String> data = parseCSVLine( line );
					if ( data.size() > 1 ) {
						
						candidate.setField1( data.get( 0 ) );
						candidate.setField2( data.get( 1 ) );
					} else {
						candidate.setField1( data.get( 0 ) );
					}
					candidates.add( candidate );
				}
			}
		}
		scanner.close();
		return candidates;
	}

	/**
	 * 
	 * @param line
	 * @return 
	 */
	protected static List<String> parseCSVLine( String line ) {
		List<String> columns = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		boolean quoted = false;
		for ( int i = 0; i < line.length(); i++ ) {
			if ( line.charAt( i ) == ',' && !quoted ) {
				columns.add( builder.toString().trim() );
				builder = new StringBuilder();
				continue;
			}
			if ( line.charAt( i ) == '"' ) {
				quoted = !quoted;
				continue;
			}
			builder.append( line.charAt( i ) );
		}
		columns.add( builder.toString() );
		return columns;
	}
}
