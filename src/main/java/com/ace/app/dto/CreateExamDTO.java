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
 * @version 0.0.1-SNAPSHOT
 */
@Getter
@Setter
public class CreateExamDTO extends BaseExamDTO {

	private MultipartFile paperDocument;
	private MultipartFile candidateLoginInfoDocument;
	private List<CreatePaperDTO> papers;
	private List<CreateCandidateDTO> candidates;
	private final Date MIN_DATE = new Date( System.currentTimeMillis() );

	private boolean hasName = false;
	private boolean hasEmail = false;
	private boolean hasPhoneNumber = false;
	private boolean sendEmail = false;


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
		} catch ( IOException e ) {
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
	 * Reads the candidate multipart file and returns a boolean based on if there were any candiadates extracted from the multipart file
	 * @return {@code true} if the parsed document has extracted exam candidates from the document, otherwise {@code false}
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
	 * Processes the string containing the papers with their questions
	 * @param text The text gotten from the file stream that contains the papers and their questions.
	 * @param self The instance of the class to be used to set show result.
	 * @return A map of paper titles with the papers attached.
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
						// Put the paper into the list
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
					else if ( line.matches("^[Aa]ns:\\s*[A-Ea-e]" ) && !handled ) {
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
	 * Processes the string containing the candidates either as a single column or a double column
	 * @param text The text gotten from the file stream.
	 * @param self The instance of the class to be used to set the login fields.
	 * @return A list of candidate credentials.
	 */
	protected static List<CreateCandidateDTO> parseCandidates( String text, CreateExamDTO self ) {
		Scanner scanner = new Scanner( text );
		List<CreateCandidateDTO> candidates = new ArrayList<>();
		boolean headRead = false;
		int email = -1, phone = -1, firstname = -1, othername = -1, lastname = -1, state = -1, appId1 = -1, appId2 = -1, papers = -1;
		while ( scanner.hasNextLine() ) {
			String line = scanner.nextLine();
			if ( line.matches( "^Evaluation Warning: The document was created with Spire\\.Doc for JAVA\\.$" ) ) {
				continue;
			}
			if ( !headRead ) {
				headRead = true;
				boolean appId1Set = false, appId2Set = false;
				List<String> columnHeads = parseCSVLine( line );
				// Figuring out the columns via the headers
				for ( int i = 0; i < columnHeads.size(); i++ ) {
					String header = columnHeads.get( i ).trim();
					if ( header.matches( "^[Aa][Pp][Pp][Ii][Dd][\\s\\S]*" ) ) {
						if ( appId1 == -1 ) {
							appId1 = i;
							header = header.replaceAll( "^[Aa][Pp]{2}[Ii][Dd]", "" ).trim();
							self.setLoginField1Desc( header );
							if ( header.matches( "[Nn][Uu][Mm]" ) ) {
								self.setLoginField1( CandidateField.Number );
								appId1Set = true;
							}
						} else if ( appId2 == -1 ) {
							appId2 = i;
							header = header.replaceAll( "^[Aa][Pp]{2}[Ii][Dd]", "" ).trim();
							self.setLoginField2Desc( header );
							if ( header.matches( "[Nn][Uu][Mm]" ) ) {
								self.setLoginField2( CandidateField.Number );
								appId2Set = true;
							} else if ( header.matches( "[Pp][Aa][Ss]{2}[Ww][Oo][Rr][Dd]" ) ) {
								self.setLoginField2( CandidateField.Password );
								appId2Set = true;
							}
						}
					}
					if ( header.matches( "([Ee]-?)?[Mm][Aa][Ii][Ll]$" ) ) {
						email = i;
						self.setHasEmail( true );
						if ( appId1 == email ) {
							self.setLoginField1( CandidateField.Email );
							appId1Set = true;
						} else if ( appId2 == email ) {
							self.setLoginField2( CandidateField.Email );
							appId2Set = true;
						}
					}
					else if ( header.matches( "([Tt][Ee][Ll][Ee][Pp][Hh][Oo][Nn][Ee]$)|([Tt][Ee][Ll]$)|([Pp][Hh][Oo][Nn][Ee]( [Nn][Uu][Mm])?)" ) ) {
						phone = i;
						self.setHasPhoneNumber( true );
						if ( appId1 == phone ) {
							self.setLoginField1( CandidateField.Telephone );
							appId1Set = true;
						} else if ( appId2 == phone ) {
							self.setLoginField2( CandidateField.Telephone );
							appId2Set = true;
						}
					}
					else if ( header.matches( "[Ff][Ii][Rr][Ss][Tt] ?[Nn][Aa][Mm][Ee]$" ) ) {
						firstname = i;
						self.setHasName( true );
					}
					else if ( header.matches( "[Ll][Aa][Ss][Tt] ?[Nn][Aa][Mm][Ee]$" ) ) {
						lastname = i;
						self.setHasName( true );
					}
					else if ( header.matches( "[Oo][Tt][Hh][Ee][Rr] ?[Nn][Aa][Mm][Ee][Ss]?$" ) ) {
						self.setHasName( true );
						othername = i;
					}
					else if ( header.matches( "[Ss][Tt][Aa][Tt][Ee]" ) ) {
						state = i;
					}
					else if ( header.matches( "[Pp][Aa][Pp][Ee][Rr][Ss]?$" ) ) {
						papers = i;
					}
				}
				if ( appId1 == -1 ) {
					scanner.close();
					throw new RuntimeException( "No 'APPID' column found!" );
				} else {
					if ( !appId1Set ) {
						self.setLoginField1( CandidateField.Text );
					}
					if ( !appId2Set && appId2 > -1 ) {
						self.setLoginField2( CandidateField.Text );
					} else if ( appId2 == -1 ) {
						self.setLoginField2( CandidateField.None );
					}
				}
				// if ( papers == -1 ) {
				// 	// scanner.close();
				// 	// throw new RuntimeException( "No 'PAPER' column found!" );
				// }
			} else {
				List<String> columns = parseCSVLine( line );
				CreateCandidateDTO candidate = new CreateCandidateDTO();
				if ( columns.size() > appId1 && !columns.get( appId1 ).isBlank() ) {
					candidate.setField1( columns.get( appId1 ) );
				} else {
					// Skip candidate if the candidate doesn't have the first AppID
					continue;
				}
				if ( appId2 > -1 && columns.size() > appId2 && !columns.get( appId2 ).isBlank() ) {
					candidate.setField2( columns.get( appId2 ) );
				} else if ( appId2 > -1 ) {
					// Skip candidate if there is meant to be an AppID data attribute for the second field
					continue;
				}
				if ( email > -1 && columns.size() > email ) {
					candidate.setEmail( columns.get( email ) );
				}
				if ( phone > -1 && columns.size() > phone ) {
					candidate.setPhoneNumber( columns.get( phone ) );
				}
				if ( firstname > -1 && columns.size() > firstname ) {
					candidate.setFirstname( columns.get( firstname ) );
				}
				if ( othername > -1 && columns.size() > othername ) {
					candidate.setOthername( columns.get( othername ) );
				}
				if ( lastname > -1 && columns.size() > lastname ) {
					candidate.setLastname( columns.get( lastname ) );
				}
				if ( state > -1 && columns.size() > state ) {
					candidate.setState( columns.get( state ) );
				}
				if ( papers > -1 && columns.size() > papers ) {
					// TODO pars papers as a csv
					List<String> paperNames = parseCSVLine( columns.get( papers ) );
					candidate.getPapernames().addAll( paperNames );
				}
				candidates.add( candidate );
			}
		}
		scanner.close();
		return candidates;
	}

	/**
	 * Processes the string containing the candidates either as a single column or a double column
	 * @param text The text gotten from the file stream.
	 * @param self The instance of the class to be used to set the login fields.
	 * @return A list of candidate credentials.
	 */
	protected static List<CreateCandidateDTO> parseCandidate( String text, CreateExamDTO self ) {
		Scanner scanner = new Scanner( text );
		List<CreateCandidateDTO> candidates = new ArrayList<>();
		boolean firstLine = true;
		boolean singleColumn = false;
		int emailIndex = -1;
		int phoneNumberIndex = -1;
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
					// scanner.close();
					// return candidates;
					continue;
				}
				// Getting the email and phonenumber indices
				for ( String col : columnHeads ) {
					if ( col.matches( "^[\\S\\s]*[Mm][Aa][Ii][Ll][\\S\\s]*$" ) ) {
						emailIndex = columnHeads.indexOf( col );
					}
					if ( col.matches( "^[\\S\\s]*[Pp][Hh][Oo][Nn][Ee][\\S\\s]*$" ) ) {
						phoneNumberIndex = columnHeads.indexOf( col );
					}
				}
				if ( columnHeads.size() == 1 ) {
					singleColumn = true;
					String str = columnHeads.get( 0 );
					if ( !str.isBlank() && self.getLoginField1Desc().isBlank() ) {
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
					if ( !str1.isBlank() && self.getLoginField1Desc().isBlank() ) {
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
					if ( !str2.isBlank() && self.getLoginField2Desc().isBlank() ) {
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
					if ( emailIndex == 0 ) {
						candidate.setEmail( str );
					} else if ( phoneNumberIndex == 0 ) {
						candidate.setPhoneNumber( str );
					}
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
					if ( emailIndex >= 0 && data.size() > emailIndex ) {
						candidate.setEmail( data.get( emailIndex ) );
					}
					if ( phoneNumberIndex >= 0 && data.size() > phoneNumberIndex ) {
						candidate.setPhoneNumber( data.get( phoneNumberIndex ) );
					}
					candidates.add( candidate );
				}
			}
		}
		scanner.close();
		return candidates;
	}

	/**
	 * Takes in a comma seperated line and seperates it into the individual data entries
	 * @param line A comma seperated line
	 * @return The individual data columns in the CSV line in a list.
	 */
	protected static List<String> parseCSVLine( String line ) {
		List<String> columns = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		boolean quoted = false;
		for ( int i = 0; i < line.length(); i++ ) {
			char ch = line.charAt( i );
			if ( ch == ',' && !quoted ) {
				columns.add( builder.toString().trim() );
				builder = new StringBuilder();
				continue;
			}
			if ( ch == '"' ) {
				quoted = !quoted;
				continue;
			}
			builder.append( ch == 147 || ch == 148 ? '"' : ch );
		}
		columns.add( builder.toString() );
		return columns;
	}
}
