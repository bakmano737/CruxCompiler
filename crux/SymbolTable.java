package crux;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
	
	ArrayList<Symbol> symbols;
	HashMap<String, Integer> keyMap;
	SymbolTable parent;
	int depth;
    
    public SymbolTable()
    {
    	symbols = new ArrayList<Symbol>();
    	keyMap = new HashMap<String, Integer>();
    	parent = null;
    	depth = 0;
    }
    
    public SymbolTable(SymbolTable parent)
    {
    	symbols = new ArrayList<Symbol>();
    	keyMap = new HashMap<String, Integer>();
    	this.parent = parent;
    	depth = parent.getDepth()+1;
    	
    }

	public Symbol lookup(String name) throws SymbolNotFoundError
    {
		// Check for the key in the local symbol table
		if(keyMap.containsKey(name))
		{
			int index = keyMap.get(name);
			return symbols.get(index);
		}
		
		// Check for the key in the parent's table
		if(this.hasParent())
		{
	    	return this.getParent().lookup(name);
		}
		
		throw new SymbolNotFoundError(name);
    }
       
    public Symbol insert(String name) throws RedeclarationError
    {
    	Symbol newSymbol = new Symbol(name);
    	
    	// Check for the symbol in the table
    	if(this.has(newSymbol))
    	{
    		throw new RedeclarationError(newSymbol);
    	}
    	else
    	{
	    	symbols.add(newSymbol);
	    	keyMap.put(name, symbols.indexOf(newSymbol));
	    	return newSymbol;
    	}
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (hasParent())
            sb.append(parent.toString());
        
        String indent = new String();
        for (int i = 0; i < depth; i++) {
            indent += "  ";
        }
        
        for (Symbol s : this.symbols)
        {
            sb.append(indent + s.toString() + "\n");
        }
        return sb.toString();
    }
    
    public boolean hasParent()
    {
    	return (null != this.parent);
    }
    
    public SymbolTable getParent() 
    {
		return this.parent;
	}
    
    public int getDepth()
    {
    	return depth;
    }
    
    public boolean has(Symbol s)
    {
    	for(Symbol t : this.symbols)
    	{
    		if(t.name().equals(s.name()))
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
}

class SymbolNotFoundError extends Error
{
    private static final long serialVersionUID = 1L;
    private String name;
    
    SymbolNotFoundError(String name)
    {
        this.name = name;
    }
    
    public String name()
    {
        return name;
    }
}

class RedeclarationError extends Error
{
    private static final long serialVersionUID = 1L;

    public RedeclarationError(Symbol sym)
    {
        super("Symbol " + sym + " being redeclared.");
    }
}
