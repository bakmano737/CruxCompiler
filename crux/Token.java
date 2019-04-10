package crux;

public class Token
{
	public static enum Kind
	{
		AND("and"),
		OR("or"),
		NOT("not"),
		LET("let"),
		VAR("var"),
		ARRAY("array"),
		FUNC("func"),
		IF("if"),
		ELSE("else"),
		WHILE("while"),
		TRUE("true"),
		FALSE("false"),
		RETURN("return"),
		OPEN_PAREN("("),
		CLOSE_PAREN(")"),
		OPEN_BRACE("{"),
		CLOSE_BRACE("}"),
		OPEN_BRACKET("["),
		CLOSE_BRACKET("]"),
		ADD("+"),
		SUB("-"),
		MUL("*"),
		DIV("/"),
		GREATER_EQUAL(">="),
		LESSER_EQUAL("<="),
		NOT_EQUAL("!="),
		EQUAL("=="),
		GREATER_THAN(">"),
		LESS_THAN("<"),
		ASSIGN("="),
		COMMA(","),
		SEMICOLON(";"),
		COLON(":"),
		CALL("::"),

		IDENTIFIER(),
		INTEGER(),
		FLOAT(),
		ERROR(),
		EOF();

		private String default_lexeme;

		Kind()
		{
			default_lexeme = "";
		}

		Kind(String lexeme)
		{
			default_lexeme = lexeme;
		}

		public boolean hasStaticLexeme()
		{
			return !(default_lexeme.equals(""));
		}

		public static boolean matches(String lexeme)
		{
			// Look through all the token kinds,
			// Compare the passed lexeme with the
			// default lexeme for that Token Kind
			for(Token.Kind k : Token.Kind.values())
			{
				// Ignore Tokens with dynamic lexemes.
				if(k.hasStaticLexeme())
				{
					if(k.default_lexeme.equals(lexeme))
					{
						return true;
					}
				}
			}
			return false;
		}

		public static Kind match(String lexeme)
		{
			// Look through all the token kinds,
			// Compare the passed lexeme with the
			// default lexeme for that Token Kind
			for(Token.Kind k : Token.Kind.values())
			{
				// Ignore Tokens with dynamic lexemes.
				if(k.hasStaticLexeme())
				{
					if(k.default_lexeme.equals(lexeme))
					{
						return k;
					}
				}
			}
			return null;
		}

		public static boolean isSpecial(String lexeme)
		{
			// Returns true if the lexeme could be the start of one of
			// the four special cases >=, <=, ==, and ::
			return ((matches(lexeme + "=")) || (matches(lexeme + ":")));
		}
	}

	private int lineNum;
	private int charPos;
	private String lexeme = "";
	Kind kind;

	private Token(int lineNum, int charPos)
	{
		this.lineNum = lineNum;
		this.charPos = charPos;

		// if we don't match anything, signal error
		this.kind = Kind.ERROR;
		this.lexeme = "No Lexeme Given";
	}

	public Token(String lexeme, int lineNum, int charPos)
	{
		this.lineNum = lineNum;
		this.charPos = charPos;

		// Check if the lexeme matches a Token's static lexeme
		if(Kind.matches(lexeme))
		{
			// If the token matches, but is only one character
			// lookout for special cases like ==, >=, <=, and ::

			// If the lexeme does match a Token, figure out which one
			Kind k = Kind.match(lexeme);
			if(null != k)
			{
				this.kind = k;
				this.lexeme = lexeme;
			}
		}
		else
		{
			// If the lexeme doesn't match it must be
			// an identifier, integer, float, or error.
			// Get the characters of the lexeme
			char[] c = lexeme.toCharArray();

			// If the first character is a digit it can't be an identifier
			if(isDigit(c[0]))
			{
				// First character is a digit, look for a decimal point
				for(int i = 1; i < c.length; i++)
				{
					if('.' == c[i])
					{
						//If any of the characters are a decimal, we have a float
						makeFLOAT(lexeme);
						return;
					}
					else if(!(isDigit(c[i])))
					{
						// If any of the subsequent characters aren't
						// a decimal or a digit, then the token is ERROR
						makeERROR(lexeme);
						return;
					}
				}
				// To get here we have,
				// First character is a digit
				// All of the remaining characters are digits
				makeINT(lexeme);
				return;
			}
			else if(isLetter(c[0]))
			{
				for(int i = 1; i < c.length; i++)
				{
					if(!(isLetter(c[i])))
					{
						// If any of the subsequent characters aren't
						// a valid character, then the token is ERROR
						makeERROR(lexeme);
						return;
					}
				}
				// The first character is valid
				// The subsequent characters are valid
				// Therefore, the lexeme must be an Identifier
				makeIDENTIFIER(lexeme);
				return;
			}
			else
			{
				// Lexeme doesn't match, first character isn't digit, letter, or '_'
				// Must be an error
				makeERROR(lexeme);
				return;
			}
		}
	}

	public static Token EOF(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.EOF;
		tok.lexeme = "EOF";
		return tok;
	}
	
	public static Token ERROR(String lexeme, int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.lexeme = "ILLEGAL LEXEME: "+lexeme;
		return tok;
	}

	private void makeERROR(String lexeme)
	{
		this.kind = Kind.ERROR;
		this.lexeme = "Unrecognized lexeme: " + lexeme;

	}

	private void makeFLOAT(String lexeme)
	{
		this.kind = Kind.FLOAT;
		this.lexeme = lexeme;
	}

	private void makeINT(String lexeme)
	{
		this.kind = Kind.INTEGER;
		this.lexeme = lexeme;
	}

	private void makeIDENTIFIER(String lexeme)
	{
		this.kind = Kind.IDENTIFIER;
		this.lexeme = lexeme;
	}

	public int lineNumber()
	{
		return lineNum;
	}

	public int charPosition()
	{
		return charPos;
	}

	// Return the lexeme representing or held by this token
	public String lexeme()
	{
		return lexeme;
	}

	public String toString()
	{
		if((Kind.IDENTIFIER == this.kind) || (Kind.INTEGER == this.kind) || (Kind.FLOAT == this.kind))
		{
			return this.kind.name() + '(' + this.lexeme + ')' + "lineNum: " + lineNum + ", charPos: " + charPos + ')';
		}
		return this.kind.name() + '(' + "lineNum: " + lineNum + ", charPos: " + charPos + ')';
	}

	public boolean isEOF()
	{
		if(Kind.EOF == this.kind)
		{
			return true;
		}
		return false;
	}

	public boolean is(Kind kind)
	{
		if(this.kind == kind) return true;
		return false;
	}

	private boolean isDigit(char c)
	{
		return (48 <= c) && (57 >= c);
	}

	private boolean isLetter(char c)
	{
		return (((65 <= c) && (90 >= c)) || ((97 <= c) && (122 >= c)) || (95 <= c));
	}
}
