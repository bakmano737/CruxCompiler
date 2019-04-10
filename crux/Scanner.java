package crux;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public class Scanner implements Iterable<Token> {
	public static String studentName = "James V. Soukup";
	public static String studentID = "48114397";
	public static String uciNetID = "jsoukup";
	
	private int lineNum;  // current line count
	private int charPos;  // character offset for current line
	private int nextChar; // contains the next char (-1 == EOF)
	private Reader input;
	private int firstCharPos;
	private String lex;
	
	Scanner(Reader reader)
	{
		// Initialize the Scanner
		lineNum = 1;
		charPos = 0;
		firstCharPos = charPos;
		input = reader;
		lex = "";
		
		// Read the first character into nextChar
		try
		{
			if(input.ready())
			{
				nextChar = readChar();
			}
			else
			{
				System.err.println("Reader Not Ready!");
				System.exit(-2);
			}
		}
		catch(IOException e)
		{
            e.printStackTrace();
            System.err.println("Error reading from the reader.");
            System.exit(-2);
		}
	}

	/* Invariants:
	 *  1. call assumes that nextChar is already holding an unread character
	 *  2. return leaves nextChar containing an untokenized character
	 */
	public Token next()
	{
		firstCharPos = charPos;
		
		// Assume nextChar already has a character
		// Converting EOF to string will cause an
		// error, check for EOF first
		if(-1 == nextChar)
		{
			// The current character is EOF.
			// Generate EOF Token and Quit
			return Token.EOF(lineNum, firstCharPos);
		}
		
		// Not EOF - Convert to string
		lex = char2string(nextChar);
		nextChar = readChar();
		
		// Clear whitespace and newlines between tokens
		spaceHandler();
		
		// Deal with possible comments
		while(isComment())
		{
			// A comment can be ignored
			// ends at newline
			commentHandler();
			if("EOF" == lex)
			{
				return Token.EOF(lineNum, charPos); 
			}
			firstCharPos = charPos;
		}
		
		// Check for single character token matches
		// Loop in case the match corresponds to a
		// comment.
		while(Token.Kind.matches(lex))
		{
			// Check for the four special cases
			// ==, >=, <=, and ::
			if(Token.Kind.matches(lex+char2string(nextChar)))
			{
				/*
				 * The next character added to the matching string
				 * still matches the default lexeme for a Token,
				 * therefore, we want to take that Token.
				 */
				lex+=char2string(nextChar);
				nextChar = readChar();
				return new Token(lex, lineNum, firstCharPos);
			}
			else if(isComment())
			{
				// A comment can be ignored
				// ends at newline
				commentHandler();
				if("EOF" == lex)
				{
					return Token.EOF(lineNum, charPos); 
				}
				firstCharPos = charPos;
			}
			else 
			{
				// If it's not a comment string then
				// it's a valid token
				// nextChar is already primed
				return new Token(lex, lineNum, firstCharPos);
				
			}
		}
		
		// Loop until we have a token
		while(!(Token.Kind.matches(lex)))
		{			
			// Is nextChar EOF?
			if(-1 == nextChar)
			{
				// The current character is EOF.
				// Generate EOF Token and Quit
				return Token.EOF(lineNum, firstCharPos);
			}
			
			if(spaceHandler()){continue;}
			
			if(isLegal(lex))
			{
				// Is it a
				if(isLegalID(lex))
				{
					while(isLegalID(char2string(nextChar)))
					{
						lex+=char2string(nextChar);
						nextChar = readChar();
						continue;
					}
					if(isTerminator(nextChar))
					{
						// Whitespace is a lexeme we can ignore
						// Pass the current value of lex to the
						// Token constructor, if lex is invalid,
						// the Token class will take care of it.
						return new Token(lex, lineNum, firstCharPos);
						
					}
					else
					{
						return Token.ERROR(lex, lineNum, firstCharPos);
					}
				}
				else if(isDigit(lex))
				{
					// lex is legal, but not an identifier
					// It must be a integer or float
					while(isDigit(lex+char2string(nextChar)))
					{
						lex+=char2string(nextChar);
						nextChar = readChar();
						continue;
					}
					if(isTerminator(nextChar))
					{
						// Whitespace is a lexeme we can ignore
						// Pass the current value of lex to the
						// Token constructor, if lex is invalid,
						// the Token class will take care of it.
						return new Token(lex, lineNum, firstCharPos);
						
					}
					else
					{
						return Token.ERROR(lex, lineNum, firstCharPos);
					}
				}
				else
				{
					// This code should be unreachable
					return Token.ERROR(lex, lineNum, firstCharPos);
				}
			}
			else
			{
				return Token.ERROR(lex, lineNum, firstCharPos);
			}
		}
		
		// To get here, lex must match a reserved keyword
		return new Token(lex, lineNum, firstCharPos);
		
	}	
	
	private int readChar() 
	{
		// Create a temporary character holder
		// Initialized to -2 because that value
		// can not be returned by Reader.read()
		int tempChar = -2;
		
		// Check for new line in the old character
		if('\n' == (char)nextChar)
		{
			// The last character read was new line,  
			// reset the character position
			charPos = 0;
		}
		
		// Read the next character
		try{tempChar = input.read();}
		catch(IOException e)
		{
            e.printStackTrace();
            System.err.println("Error reading from the reader.");
            System.exit(-2);
		}
		
		// To get here we have successfully read a character
		// Update the character position value
		charPos++;
		
		// Check for new line in the new character
		if('\n' == (char)tempChar)
		{
			// The character read is new line, 
			// increment the line number
			lineNum++;
		}
		
		return tempChar;
		
	}
	
	private boolean spaceHandler() 
	{
		boolean tmp = false;
		while(this.lex.equals(" ") || this.lex.equals("\n"))
		{
			tmp = true;
			if(lex.equals(" "))
			{
				firstCharPos++;
			}
			else
			{
				firstCharPos = 1;
			}
			lex = char2string(nextChar);
			nextChar = readChar();
		}
		return tmp;
	}

	private boolean isComment()
	{
		return (lex.equals("/") && char2string(nextChar).equals("/"));
	}
	
	private void commentHandler()
	{
		// A comment can be ignored
		// ends at newline
		while(!(char2string(nextChar).equals("\n")))
		{
			// Check for EOF
			if(-1 == nextChar)
			{
				// The current character is EOF.
				// Generate EOF Token and Quit
				lex = "EOF";
			}						
			nextChar = readChar();
			firstCharPos++;
		}
		
		// The comment is empty, get a new lexeme and character
		lex = freshLex();
		nextChar = readChar();
	}
	
	private static String char2string(int nchar)
	{
		if(-1 == nchar)
		{
			return "EOF";
		}
		return String.valueOf((char)nchar);
	}
	
	private static boolean isLegal(String lexeme)
	{
		/*
		 * Only digit, letter, and "_" are allowed
		 */
		for(char c : lexeme.toCharArray())
		{
			if(!(isDigit(c) || isLetter(c) || ('_' == c)))
			{
				return false;
			}
		}
		return true;
	}
	
	private static boolean isLegalID(String lexeme)
	{
		/*
		 * Only digit, letter, and "_" are allowed
		 */
		if(!isLegalFirst(lexeme))
		{
			return false;
		}
		for(char c : lexeme.toCharArray())
		{
			if(!(isDigit(c) || isLetter(c) || ('_' == c)))
			{
				return false;
			}
		}
		return true;
	}
	
	private static boolean isLegalFirst(String lexeme)
	{
		/*
		 * Only digit, letter, and "_" are allowed
		 */
		return (isLetter(lexeme.charAt(0)) || lexeme.equals("_"));
	}
	
	private static boolean isTerminator(int readChar)
	{
		/*
		 * Whitespace, newline, EOF, and the special reserved
		 * sequences terminate a token
		 */
		String tmpLex = char2string(readChar);
		return (tmpLex.equals(" ") || tmpLex.equals("\n") || 
				tmpLex.equals("EOF") || isSingleChar(readChar));
		
	}
	
	private static boolean isSingleChar(int readChar)
	{
		/*
		 * ( ) { } [ ] + - / * > < = , ; :
		 */
		String tmpLex = char2string(readChar);
		return (Token.Kind.matches(tmpLex));
	}

	private static boolean isDigit(char c)
	{
		return (48 <= c) && (57 >= c);
	}

	private static boolean isDigit(String lexeme)
	{
		if(!((48 <= lexeme.charAt(0)) && (57 >= lexeme.charAt(0))))
		{
			return false;
		}
		for(char c : lexeme.toCharArray())
		{
			if(!(isDigit(c) || '.' == c))
			{
				return false;
			}
		}
		return true;
	}

	private static boolean isLetter(char c)
	{
		return (((65 <= c) && (90 >= c))
			||  ((97 <= c) && (122 >= c)) || (95 <= c));
	}
	
	private String freshLex()
	{
		// Get a new character
		nextChar = readChar();
		// Is nextChar EOF?
		if(-1 == nextChar)
		{
			// The current character is EOF.
			// Generate EOF Token and Quit
			return "EOF";
		}
		// Start a fresh lexeme
		return String.valueOf((char)nextChar);
	}

	// This method is only here to make Eclipse happy.
	@Override
	public Iterator<Token> iterator() {
		return null;
	}

	
}
