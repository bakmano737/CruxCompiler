package crux;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
	public static String studentName = "James V. Soukup";
    public static String studentID = "48114397";
    public static String uciNetID = "jsoukup";
     
// Grammar Rule Reporting ==========================================
    private int parseTreeRecursionDepth = 0;
    private StringBuffer parseTreeBuffer = new StringBuffer();

    public void enterRule(NonTerminal nonTerminal) {
        String lineData = new String();
        for(int i = 0; i < parseTreeRecursionDepth; i++)
        {
            lineData += "  ";
        }
        lineData += nonTerminal.name();
        //System.out.println("descending " + lineData);
        parseTreeBuffer.append(lineData + "\n");
        parseTreeRecursionDepth++;
    }

    private void exitRule(NonTerminal nonTerminal)
    {
        parseTreeRecursionDepth--;
    }

    public String parseTreeReport()
    {
        return parseTreeBuffer.toString();
    }

// Error Reporting ==========================================
    private StringBuffer errorBuffer = new StringBuffer();

    private String reportSyntaxError(NonTerminal nt)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected a token from " + nt.name() + " but got " + currentToken.kind.name() + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }

    private String reportSyntaxError(Token.Kind kind)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected " + kind + " but got " + currentToken.kind.name() + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }

    public String errorReport()
    {
        return errorBuffer.toString();
    }

    public boolean hasError()
    {
        return errorBuffer.length() != 0;
    }

    private class QuitParseException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        public QuitParseException(String errorMessage) {
            super(errorMessage);
        }
    }

    private int lineNumber()
    {
        return currentToken.lineNumber();
    }

    private int charPosition()
    {
        return currentToken.charPosition();
    }

// Parser ==========================================
    private Scanner scanner;
    private Token currentToken;

    public Parser(Scanner scanner)
    {
    	// Get the first token from the scanner
    	this.scanner = scanner;
    	currentToken = this.scanner.next();
    }

    public void parse()
    {
        initSymbolTable();
        try {
            program();
        } catch (QuitParseException q) {
            errorBuffer.append("SyntaxError(" + lineNumber() + "," + charPosition() + ")");
            errorBuffer.append("[Could not complete parsing.]");
        }
    }

// Helper Methods ==========================================

    private boolean have(Token.Kind kind)
    {
        return currentToken.is(kind);
    }

    private boolean have(NonTerminal nt)
    {
        return nt.firstSet().contains(currentToken.kind);
    }

    private boolean accept(Token.Kind kind)
    {
        if (have(kind)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }

    private boolean accept(NonTerminal nt)
    {
        if (have(nt)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }

    private boolean expect(Token.Kind kind)
    {
        if (accept(kind))
            return true;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
        //return false;
    }

    private boolean expect(NonTerminal nt)
    {
        if (accept(nt))
            return true;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
        //return false;
    }


    private Token expectRetrieve(Token.Kind kind)
    {
        Token tok = currentToken;
        if (accept(kind))
            return tok;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
        //return ErrorToken(errorMessage);
    }
        
    private Token expectRetrieve(NonTerminal nt)
    {
        Token tok = currentToken;
        if (accept(nt))
            return tok;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
        //return ErrorToken(errorMessage);
    }    
    
// Grammar Rules =====================================================
    // TODO: Rewrite grammar rules for symbol table
    // literal := INTEGER | FLOAT | TRUE | FALSE .
    public void literal()
    {        
        // Start by calling enterRule
        enterRule(NonTerminal.LITERAL);
        
        // Use accept to identify the token
        if(have(Token.Kind.INTEGER))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.LITERAL);
        	// return;
        	
        	expect(Token.Kind.INTEGER);
        }
        else if(have(Token.Kind.FLOAT))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.LITERAL);
        	// return;
        	
        	expect(Token.Kind.FLOAT);
        }
        else if(have(Token.Kind.TRUE))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.LITERAL);
        	// return;
        	
        	expect(Token.Kind.TRUE);
        }
        else if(have(Token.Kind.FALSE))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.LITERAL);
        	// return;
        	
        	expect(Token.Kind.FALSE);
        }
        else
        {
        	// ERROR
        	throw new QuitParseException("In literal, no literal found");
        }
        
        // throw new RuntimeException("Left the if/else tree: literal");
        // The rule is finished call exitRule
        exitRule(NonTerminal.LITERAL);
    }

    // designator := IDENTIFIER { "[" expression0 "]" } .
    public void designator()
    {
        enterRule(NonTerminal.DESIGNATOR);

        Token tok = expectRetrieve(Token.Kind.IDENTIFIER);
        tryResolveSymbol(tok);
        
        while (accept(Token.Kind.OPEN_BRACKET)) {
            expression0();
            expect(Token.Kind.CLOSE_BRACKET);
        }

        exitRule(NonTerminal.DESIGNATOR);
    }
    
    // type := IDENTIFIER .
    public void type()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.TYPE);
    	// Only one possible terminal token kind
    	expect(Token.Kind.IDENTIFIER);
    	// To get here the token is correct
    	// finish by calling exitRule
    	exitRule(NonTerminal.TYPE);
    }

    // op0 := ">=" | "<=" | "!=" | "==" | ">" | "<" .
    public void op0()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.OP0);
    	
    	// Use accept to identify the token
        if(have(Token.Kind.GREATER_EQUAL))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP0);
        	// return;
        	
        	expect(Token.Kind.GREATER_EQUAL);
        }
        else if(have(Token.Kind.LESSER_EQUAL))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP0);
        	// return;
        	
        	expect(Token.Kind.LESSER_EQUAL);
        }
        else if(have(Token.Kind.NOT_EQUAL))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP0);
        	// return;
        	
        	expect(Token.Kind.NOT_EQUAL);
        }
        else if(have(Token.Kind.EQUAL))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP0);
        	// return;
        	
        	expect(Token.Kind.EQUAL);
        }
        else if(have(Token.Kind.GREATER_THAN))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP0);
        	// return;
        	
        	expect(Token.Kind.GREATER_THAN);
        }
        else if(have(Token.Kind.LESS_THAN))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP0);
        	// return;
        	
        	expect(Token.Kind.LESS_THAN);
        }
        else
        {
        	// ERROR
        	throw new QuitParseException("In OP0, no op found");
        }
    	
        // throw new RuntimeException("Left the if/else tree: OP0");
        // The rule is finished call exitRule
        exitRule(NonTerminal.OP0);
    }
    
    // op1 := "+" | "-" | "or" .
    public void op1()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.OP1);
    	
    	// Use accept to identify the token
        if(have(Token.Kind.ADD))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP1);
        	// return;
        	
        	expect(Token.Kind.ADD);
        }
        else if(have(Token.Kind.SUB))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP1);
        	// return;
        	
        	expect(Token.Kind.SUB);
        }
        else if(have(Token.Kind.OR))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP1);
        	// return;
        	
        	expect(Token.Kind.OR);
        }
        else
        {
        	// ERROR
        	throw new QuitParseException("In OP1, no op found");
        }
    	
        // throw new RuntimeException("Left the if/else tree: OP1");
        // The rule is finished, call exitRule
        exitRule(NonTerminal.OP1);
    }
    
    // op2 := "*" | "/" | "and" .
    public void op2()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.OP2);
    	
    	// Use accept to identify the token
        if(have(Token.Kind.MUL))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP2);
        	// return;
        	
        	expect(Token.Kind.MUL);
        }
        else if(accept(Token.Kind.DIV))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP2);
        	// return;
        }
        else if(have(Token.Kind.AND))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP2);
        	// return;
        	
        	expect(Token.Kind.AND);
        }
        else
        {
        	// ERROR
        	throw new QuitParseException("In OP2, no op found");
        }
        
        // throw new RuntimeException("Left the if/else tree: OP2");
        // The rule is finished, call exitRule
        exitRule(NonTerminal.OP2);
    }
    
    // expression0 := expression1 [ op0 expression1 ] .
    public void expression0()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.EXPRESSION0);
    	
    	// expression0 must start with expression1, verify this
    	// expect(NonTerminal.EXPRESSION1);
    	expression1();
    	
    	
    	// Check for the optional op0 and expression1
    	if(have(NonTerminal.OP0))
    	{
    		// If the op0 is there, expression1 must be as well
    		// expect(NonTerminal.EXPRESSION1);
    		op0();
    		expression1();
    	}
    	
    	// If something had gone wrong, one of the expect calls
    	// would have thrown an error. Call exitRule before returning
    	exitRule(NonTerminal.EXPRESSION0);
    }
    
    // expression1 := expression2 { op1  expression2 } .
    public void expression1()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.EXPRESSION1);
    	
    	// expression1 must start with expression2, verify this
    	// expect(NonTerminal.EXPRESSION2);
    	expression2();
    	
    	// Check for the optional multiples of op1 and expression2
    	while(have(NonTerminal.OP1))
    	{
    		// If the op1 is there, expression2 must be as well
    		// expect(NonTerminal.EXPRESSION2);
    		op1();
    		expression2();
    	}

    	// If something had gone wrong, one of the expect calls
    	// would have thrown an error. Call exitRule before returning
    	exitRule(NonTerminal.EXPRESSION1);
    }
    
    // expression2 := expression3 { op2 expression3 } .
    public void expression2()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.EXPRESSION2);
    	
    	// expression2 must start with expression3, verify this
    	// expect(NonTerminal.EXPRESSION3);
    	expression3();
    	
    	// Check for the optional multiples of op2 and expression3
    	while(have(NonTerminal.OP2))
    	{
    		// If the op2 is there, expression3 must be as well
    		// expect(NonTerminal.EXPRESSION3);
    		op2();
    		expression3();
    	}

    	// If something had gone wrong, one of the expect calls
    	// would have thrown an error. Call exitRule before returning
    	exitRule(NonTerminal.EXPRESSION2);
    }
    
    /*expression3 := "not" expression3
    | "(" expression0 ")"
    | designator
    | call-expression
    | literal .*/
    public void expression3()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.EXPRESSION3);
    	
    	// Check for not
    	if(accept(Token.Kind.NOT))
    	{
    		// "not" must be succeeded by expression3
    		// expect(NonTerminal.EXPRESSION3);
    		// Finished with this rule, call exitRule
    		// exitRule(NonTerminal.EXPRESSION3);
        	// return;
    		
    		expression3();
    	}
    	else if(accept(Token.Kind.OPEN_PAREN)) // Check for "("
    	{
    		// "(" is succeeded only by expression0 and ")"
    		// expect(NonTerminal.EXPRESSION0);
    		// expect(Token.Kind.CLOSE_PAREN);
    		// Finished with this rule, call exitRule
    		// exitRule(NonTerminal.EXPRESSION3);
        	// return;
    		
    		expression0();
    		expect(Token.Kind.CLOSE_PAREN);
    	}
    	else if(have(NonTerminal.DESIGNATOR))
    	{
    		// Finished with this rule, call exitRule
    		// exitRule(NonTerminal.EXPRESSION3);
        	// return;
    		
    		designator();
    	}
    	else if(have(NonTerminal.CALL_EXPRESSION))
    	{
    		// Finished with this rule, call exitRule
    		// exitRule(NonTerminal.EXPRESSION3);
        	// return;
    		
    		call_expression();
    	}
    	else if(have(NonTerminal.LITERAL))
    	{
    		// Finished with this rule, call exitRule
    		// exitRule(NonTerminal.EXPRESSION3);
        	// return;
    		
    		literal();
    	}
    	else
    	{
    		// ERROR
        	throw new QuitParseException("In expression3, no matches!");
    		
    	}
    	
    	// throw new RuntimeException("Left the if/else tree: expression3");
    	// The rule is finished, call exitRule
    	exitRule(NonTerminal.EXPRESSION3);
    }
    
    // call-expression := "::" IDENTIFIER "(" expression-list ")" .
    public void call_expression()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.CALL_EXPRESSION);
    	
    	// Must start with "::" - CALL token
    	expect(Token.Kind.CALL);
    	// Must be followed by an IDENTIFIER
    	Token tok = expectRetrieve(currentToken.kind);
    	tryResolveSymbol(tok);
    	// Must be followed by "(" - OPEN_PAREN
    	expect(Token.Kind.OPEN_PAREN);
    	// Must be followed by an expression-list
    	// expect(NonTerminal.EXPRESSION_LIST);
    	expression_list();
    	// Must be followed by ")" - CLOSE_PAREN
    	expect(Token.Kind.CLOSE_PAREN);
    	
    	// To get here we have finished the rule
    	exitRule(NonTerminal.CALL_EXPRESSION);
    }
    
    // expression-list := [ expression0 { "," expression0 } ] .
    public void expression_list()
    {
    	// Start by calling enterRule()
    	enterRule(NonTerminal.EXPRESSION_LIST);
    	
    	// Check for the optional expression0
    	if(have(NonTerminal.EXPRESSION0))
    	{
    		expression0();
    		// expression0 may be followed by 
    		// any number of "," - COMMA
    		while(accept(Token.Kind.COMMA))
    		{
    			// If "," - COMMA is there, 
    			// it must be followed by expression0
    			// expect(NonTerminal.EXPRESSION0);
    			expression0();
    		}
    	}
    	
    	// The rule is finished call exitRule
    	exitRule(NonTerminal.EXPRESSION_LIST);
    }
    
    // parameter := IDENTIFIER ":" type .
    public void parameter()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.PARAMETER);
    	
    	// Must start with an IDENTIFIER
    	Token tok = expectRetrieve(currentToken.kind);
    	tryDeclareSymbol(tok);
    	// Must be followed by ":" - COLON
    	expect(Token.Kind.COLON);
    	// Must be followed by a type
    	// expect(NonTerminal.TYPE);
    	type();
    	
    	// To get here we have finished the rule
    	exitRule(NonTerminal.PARAMETER);
    }
    
    // parameter-list := [ parameter { "," parameter } ] .
    public void parameter_list()
    {
    	// Start by calling enterRule()
    	enterRule(NonTerminal.PARAMETER_LIST);
    	
    	// Check for the optional parameter
    	if(have(NonTerminal.PARAMETER))
    	{
    		parameter();
    		// parameter may be followed by 
    		// any number of "," - COMMA
    		while(accept(Token.Kind.COMMA))
    		{
    			// If "," - COMMA is there, 
    			// it must be followed by parameter
    			// expect(NonTerminal.PARAMETER);
    			parameter();
    		}
    	}
    	
    	// The rule is finished call exitRule
    	exitRule(NonTerminal.PARAMETER_LIST);
    }
    
    // variable-declaration := "var" IDENTIFIER ":" type ";" .
    public void variable_declaration()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.VARIABLE_DECLARATION);
    	
    	// Must start with "var" - VAR
    	expect(Token.Kind.VAR);
    	// Must be followed by an IDENTIFIER
    	Token tok = expectRetrieve(currentToken.kind);
    	tryDeclareSymbol(tok);
    	// Must be followed by ":" - COLON
    	expect(Token.Kind.COLON);
    	// Must be followed by a type
    	// expect(NonTerminal.TYPE);
    	type();
    	// Must be followed by ";" - SEMICOLON
    	expect(Token.Kind.SEMICOLON);
    	
    	// To get here we have finished the rule
    	exitRule(NonTerminal.VARIABLE_DECLARATION);
    }
    
    // array-declaration := "array" IDENTIFIER ":" type "[" INTEGER "]" { "[" INTEGER "]" } ";" .
    public void array_declaration()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.ARRAY_DECLARATION);
    	
    	// Must start with "array" - ARRAY
    	expect(Token.Kind.ARRAY);
    	// Must be followed by an IDENTIFIER
    	Token tok = expectRetrieve(currentToken.kind);
    	tryDeclareSymbol(tok);
    	// Must be followed by ":" - COLON
    	expect(Token.Kind.COLON);
    	// Must be followed by a type
    	// expect(NonTerminal.TYPE);
    	type();
    	// Must be followed by "[" - OPEN_BRACKET
    	expect(Token.Kind.OPEN_BRACKET);
    	// Must be followed by INTEGER
    	expect(Token.Kind.INTEGER);
    	// Must be followed by "]" - CLOSE_BRACKET
    	expect(Token.Kind.CLOSE_BRACKET);
    	// May be followed by any number of "[" - OPEN_BRACKET
    	while(accept(Token.Kind.OPEN_BRACKET))
    	{
    		// Must be followed by INTEGER
    		expect(Token.Kind.INTEGER);
    		// Must be followed by "]" - CLOSE_BRACKET
    		expect(Token.Kind.CLOSE_BRACKET);
    	}
    	// Must be followed by ";" - SEMICOLON
    	expect(Token.Kind.SEMICOLON);
    	
    	// To get here we have finished the rule
    	exitRule(NonTerminal.ARRAY_DECLARATION);
    }
    
    // function-definition := "func" IDENTIFIER "(" parameter-list ")" ":" type statement-block .
    public void function_definition()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.FUNCTION_DEFINITION);
    	
    	// Must start with "func" - FUNC
    	expect(Token.Kind.FUNC);
    	// Must be followed by an IDENTIFIER
    	Token tok = expectRetrieve(currentToken.kind);
    	tryDeclareSymbol(tok);
    	// Must be followed by "(" - OPEN_PAREN
    	expect(Token.Kind.OPEN_PAREN);
    	// Must be followed by a parameter-list
    	
    	// Enter a new scope for the function
    	enterScope();
    	// expect(NonTerminal.PARAMETER_LIST);
    	parameter_list();
    	// Must be followed by ")" - CLOSE_PAREN
    	expect(Token.Kind.CLOSE_PAREN);
    	// Must be followed by ":" - COLON
    	expect(Token.Kind.COLON);
    	// Must be followed by a type
    	// expect(NonTerminal.TYPE);
    	type();

    	// Must be followed by a statement-block
    	statement_block();
    	
    	// Exit the function scope
    	exitScope();
    	
    	// To get here we have finished the rule
    	exitRule(NonTerminal.FUNCTION_DEFINITION);
    }
    
    // declaration := variable-declaration | array-declaration | function-definition .
    public void declaration()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.DECLARATION);
    	
    	// Use accept to identify the token
        if(have(NonTerminal.VARIABLE_DECLARATION))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP2);
        	// return;
        	variable_declaration();
        }
        else if(have(NonTerminal.ARRAY_DECLARATION))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP2);
        	// return;
        	array_declaration();
        }
        else if(have(NonTerminal.FUNCTION_DEFINITION))
        {
        	// To get here the token is correct
        	// finish by calling exitRule
        	// exitRule(NonTerminal.OP2);
        	// return;
        	function_definition();
        }
        else
        {
        	// ERROR
        	throw new QuitParseException("In declaration, no matches!");
        }
        
        // throw new RuntimeException("Left the if/else tree: OP2");
        // The rule is finished, call exitRule
        exitRule(NonTerminal.DECLARATION);
    }
    
    // declaration-list := { declaration } .
    public void declaration_list()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.DECLARATION_LIST);
    	
    	// Any number of declaration
    	while(have(NonTerminal.DECLARATION))
    	{
    		declaration();
    	}
    	
    	// The rule is finished, call exitRule
    	exitRule(NonTerminal.DECLARATION_LIST);
    }
    
    // assignment-statement := "let" designator "=" expression0 ";" .
    public void assignment_statement()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.ASSIGNMENT_STATEMENT);
    	
    	// Expect a LET token
    	expect(Token.Kind.LET);
    	// Enter designator
    	designator();
    	// expect a ASSIGN token
    	expect(Token.Kind.ASSIGN);
    	// Enter expression0
    	expression0();
    	// Expect a SEMICOLON token
    	expect(Token.Kind.SEMICOLON);
    	
    	// The rule is finished, call exitRule
    	exitRule(NonTerminal.ASSIGNMENT_STATEMENT);
    }
    
    // call-statement := call-expression ";" .
    public void call_statement()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.CALL_STATEMENT);
    	
    	// Enter call_expression
    	call_expression();
    	// Expect a SEMICOLON token
    	expect(Token.Kind.SEMICOLON);
    	
    	// The rule is finished, call exitRule
    	exitRule(NonTerminal.CALL_STATEMENT);
    }
    
    // if-statement := "if" expression0 statement-block [ "else" statement-block ] .
    public void if_statement()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.IF_STATEMENT);
    	
    	// Expect IF token
    	expect(Token.Kind.IF);
    	// Enter expression0
    	expression0();
    	
    	// Enter a new scope for the statement-block
    	enterScope();
    	
    	// Enter statement-block
    	statement_block();
    	
    	// Return from the statement-block scope
    	exitScope();
    	
    	// Optional ELSE token
    	if(accept(Token.Kind.ELSE))
    	{
    		// Enter a new scope for the else statement
    		enterScope();
    		
    		// Enter statement-block
    		statement_block();
    		
    		// Exit the statement-block scope
    		exitScope();
    	}
    	
    	// The rule is finished, call exitRule
    	exitRule(NonTerminal.IF_STATEMENT);
    }
    
    // while-statement := "while" expression0 statement-block .
    public void while_statement()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.WHILE_STATEMENT);
    	
    	// Expect a WHILE token
    	expect(Token.Kind.WHILE);
    	// Enter expression0
    	expression0();
    	
    	// Enter a new scope for the statement-block
    	enterScope();
    	
    	// Enter statement-block
    	statement_block();
    	
    	// Exit scope of the statement-block
    	exitScope();
    	
    	// The rule is finished, call exitRule
    	exitRule(NonTerminal.WHILE_STATEMENT);
    }
    
    // return-statement := "return" expression0 ";" .
    public void return_statement()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.RETURN_STATEMENT);
    	
    	// Expect a RETURN token
    	expect(Token.Kind.RETURN);
    	// Enter expression0
    	expression0();
    	// Expect a SEMICOLON token
    	expect(Token.Kind.SEMICOLON);
    	
    	// The rule is finished, call exitRule
    	exitRule(NonTerminal.RETURN_STATEMENT);
    }
    
    /*statement := variable-declaration
    		           | call-statement
    		           | assignment-statement
    		           | if-statement
    		           | while-statement
    		           | return-statement .*/
    public void statement()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.STATEMENT);
    	
    	// Check for variable-declaration
    	if(have(NonTerminal.VARIABLE_DECLARATION))
    	{
    		variable_declaration();
    	}
    	else if(have(NonTerminal.CALL_STATEMENT))
    	{
    		call_statement();
    	}
    	else if(have(NonTerminal.ASSIGNMENT_STATEMENT))
    	{
    		assignment_statement();
    	}
    	else if(have(NonTerminal.IF_STATEMENT))
    	{
    		if_statement();
    	}
    	else if(have(NonTerminal.WHILE_STATEMENT))
    	{
    		while_statement();
    	}
    	else if(have(NonTerminal.RETURN_STATEMENT))
    	{
    		return_statement();
    	}
    	else
    	{
    		// ERROR
    		throw new QuitParseException("No Match for statement");
    	}
    	
    	// The rule is finished, call exitRule
    	exitRule(NonTerminal.STATEMENT);
    }
    
    // statement-list := { statement } .
    public void statement_list()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.STATEMENT_LIST);
    	
    	// Any number of statement
    	while(have(NonTerminal.STATEMENT))
    	{
    		statement();
    	}
    	
    	// The rule is finished, call exitRule
    	exitRule(NonTerminal.STATEMENT_LIST);
    }
    
    // statement-block := "{" statement-list "}" .
    public void statement_block()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.STATEMENT_BLOCK);
    	
    	// Must start with "{" - OPEN_BRACE
    	if(accept(Token.Kind.OPEN_BRACE))
    	{
    		// Expand the statement-list
    		statement_list();
    		// Must be followed by "}" - CLOSE_BRACE
    		expect(Token.Kind.CLOSE_BRACE);
    	}
    	
    	// The rule is finished, call exitRule
    	exitRule(NonTerminal.STATEMENT_BLOCK);
    }
    
    // program := declaration-list EOF .
    public void program()
    {
    	// Start by calling enterRule
    	enterRule(NonTerminal.PROGRAM);
    	
    	// Create a new scope for the program
    	// enterScope();
    	
    	// Expand the declaration-list
    	declaration_list();
    	
    	// Return from the program scope
    	// exitScope();
    	
    	// After returning from declaration_list()
    	// Look for EOF
    	expect(Token.Kind.EOF);
    	
    	// The rule is finished, call exitRule
    	exitRule(NonTerminal.PROGRAM);
    }
    
// SymbolTable Management ==========================
    private SymbolTable symbolTable;
    private Stack<SymbolTable> scopeStack;
    private void initSymbolTable()
    {
    	// Initialize the global table and stack
    	symbolTable = new SymbolTable();
    	scopeStack = new Stack<SymbolTable>();
    	
    	// Add the built-in functions
    	symbolTable.insert("readInt");
    	symbolTable.insert("readFloat");
    	symbolTable.insert("printBool");
    	symbolTable.insert("printInt");
    	symbolTable.insert("printFloat");
    	symbolTable.insert("println");
    }
    
    /* 
	 * In a new scope we want to 
	 * push the current symbolTable to
	 * the stack for recall when the 
	 * new scope clears. Then we create
	 * a new SymbolTable to take the place
	 * of the current symbol table
	 */
    private void enterScope()
    {
    	scopeStack.push(symbolTable);
    	symbolTable = new SymbolTable(symbolTable);
    }
    
    /*
     * When we exit a scope we want to put
     * the last scope pushed to the stack
     * back in the place of the current symbol
     */
    private void exitScope()
    {
    	symbolTable = scopeStack.pop();
    }

    private Symbol tryResolveSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        try {
            return symbolTable.lookup(name);
        } catch (SymbolNotFoundError e) {
            String message = reportResolveSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportResolveSymbolError(String name, int lineNum, int charPos)
    {
        String message = "ResolveSymbolError(" + lineNum + "," + charPos + ")[Could not find " + name + ".]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }

    private Symbol tryDeclareSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        try {
            return symbolTable.insert(name);
        } catch (RedeclarationError re) {
            String message = reportDeclareSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportDeclareSymbolError(String name, int lineNum, int charPos)
    {
        String message = "DeclareSymbolError(" + lineNum + "," + charPos + ")[" + name + " already exists.]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }    
}
